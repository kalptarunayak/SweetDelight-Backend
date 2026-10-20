package com.sweetdelight.api.service;

import com.sweetdelight.api.config.AppProperties;
import com.sweetdelight.api.dto.CreatePaymentResponse;
import com.sweetdelight.api.dto.PaymentDto;
import com.sweetdelight.api.dto.VerifyPaymentRequest;
import com.sweetdelight.api.entity.*;
import com.sweetdelight.api.exception.BadRequestException;
import com.sweetdelight.api.exception.ForbiddenException;
import com.sweetdelight.api.exception.ResourceNotFoundException;
import com.sweetdelight.api.repository.OrderRepository;
import com.sweetdelight.api.repository.OrderStatusEventRepository;
import com.sweetdelight.api.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusEventRepository statusEventRepository;
    private final RazorpayService razorpayService;
    private final NotificationService notificationService;
    private final AppProperties appProperties;

    public PaymentService(PaymentRepository paymentRepository,
                          OrderRepository orderRepository,
                          OrderStatusEventRepository statusEventRepository,
                          RazorpayService razorpayService,
                          NotificationService notificationService,
                          AppProperties appProperties) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.statusEventRepository = statusEventRepository;
        this.razorpayService = razorpayService;
        this.notificationService = notificationService;
        this.appProperties = appProperties;
    }

    /**
     * Creates a Razorpay order or reuses the existing unpaid Razorpay order.
     */
    @Transactional
    public CreatePaymentResponse createOrGetPaymentOrder(Long orderId, UUID customerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getCustomerId().equals(customerId)) {
            throw new ForbiddenException("You are not authorized to pay for this order");
        }

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT && order.getStatus() != OrderStatus.PAYMENT_FAILED) {
            throw new BadRequestException("Order is not in a payable status. Current status: " + order.getStatus());
        }

        if (order.getPaymentMethod() != PaymentMethod.ONLINE) {
            throw new BadRequestException("Order payment method is " + order.getPaymentMethod() + ", not ONLINE");
        }

        // Amount in paise: server-computed totalInr * 100
        long amountPaise = order.getTotalInr().multiply(BigDecimal.valueOf(100)).longValue();

        // Check if an existing unpaid payment order exists for this order -> MUST reuse it!
        Optional<Payment> existingUnpaid = paymentRepository.findByOrderIdOrderByCreatedAtDesc(orderId).stream()
                .filter(p -> p.getStatus() == PaymentStatus.CREATED)
                .findFirst();

        if (existingUnpaid.isPresent()) {
            Payment payment = existingUnpaid.get();
            log.info("Reusing existing unpaid Razorpay order {} for order {}",
                    RazorpayService.maskId(payment.getRazorpayOrderId()), order.getOrderNumber());
            return new CreatePaymentResponse(
                    payment.getRazorpayOrderId(),
                    payment.getAmountPaise(),
                    payment.getCurrency(),
                    appProperties.getRazorpay().getKeyId()
            );
        }

        // Create new Razorpay order
        String razorpayOrderId = razorpayService.createRazorpayOrder(amountPaise, "INR", order.getOrderNumber());

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setRazorpayOrderId(razorpayOrderId);
        payment.setAmountPaise(amountPaise);
        payment.setCurrency("INR");
        payment.setStatus(PaymentStatus.CREATED);

        Payment saved = paymentRepository.save(payment);
        log.info("Initialized payment record for order {} with Razorpay order {}",
                order.getOrderNumber(), RazorpayService.maskId(razorpayOrderId));

        return new CreatePaymentResponse(
                saved.getRazorpayOrderId(),
                saved.getAmountPaise(),
                saved.getCurrency(),
                appProperties.getRazorpay().getKeyId()
        );
    }

    /**
     * Verifies payment signature and marks payment PAID idempotently.
     */
    @Transactional
    public PaymentDto verifyPayment(VerifyPaymentRequest request) {
        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found for Razorpay order: " + request.getRazorpayOrderId()));

        // Idempotency: if already PAID, return without reprocessing
        if (payment.getStatus() == PaymentStatus.PAID) {
            log.info("Payment {} already marked PAID. Idempotent return.", RazorpayService.maskId(payment.getRazorpayOrderId()));
            return mapToDto(payment);
        }

        // Verify HMAC-SHA256 signature
        boolean isValidSignature = razorpayService.verifyPaymentSignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
        );

        if (!isValidSignature) {
            log.warn("Invalid signature for payment verification. Order: {}, Payment: {}",
                    RazorpayService.maskId(request.getRazorpayOrderId()), RazorpayService.maskId(request.getRazorpayPaymentId()));
            throw new BadRequestException("Invalid payment signature");
        }

        // Mark payment as PAID
        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setRazorpaySignature(request.getRazorpaySignature());
        payment.setStatus(PaymentStatus.PAID);
        Payment savedPayment = paymentRepository.save(payment);

        // Transition order status to PLACED
        Order order = payment.getOrder();
        if (order.getStatus() == OrderStatus.PENDING_PAYMENT || order.getStatus() == OrderStatus.PAYMENT_FAILED) {
            order.setStatus(OrderStatus.PLACED);
            orderRepository.save(order);

            // Record status change event
            OrderStatusEvent event = new OrderStatusEvent(order, OrderStatus.PLACED,
                    "Payment verified via Razorpay ID: " + RazorpayService.maskId(request.getRazorpayPaymentId()));
            statusEventRepository.save(event);

            log.info("Order {} transitioned to PLACED after verified payment", order.getOrderNumber());

            // Trigger payment confirmed notifications to both customer and admin
            notificationService.notifyPaymentConfirmed(order, savedPayment);
        }

        return mapToDto(savedPayment);
    }

    /**
     * Admin refund endpoint: calls Razorpay refund API and sets order CANCELLED.
     */
    @Transactional
    public PaymentDto processRefund(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        Payment paidPayment = paymentRepository.findByOrderIdOrderByCreatedAtDesc(orderId).stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("No PAID payment found for order: " + orderId));

        if (paidPayment.getRazorpayPaymentId() == null) {
            throw new BadRequestException("Payment lacks a valid Razorpay payment ID to refund");
        }

        // Call Razorpay refund
        razorpayService.refundPayment(paidPayment.getRazorpayPaymentId(), paidPayment.getAmountPaise());

        paidPayment.setStatus(PaymentStatus.REFUNDED);
        Payment updatedPayment = paymentRepository.save(paidPayment);

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        OrderStatusEvent event = new OrderStatusEvent(order, OrderStatus.CANCELLED,
                "Order cancelled and refunded full amount of ₹" + order.getTotalInr());
        statusEventRepository.save(event);

        // Notify customer
        notificationService.notifyRefundProcessed(order, updatedPayment);

        return mapToDto(updatedPayment);
    }

    public PaymentDto mapToDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setOrderId(payment.getOrder() != null ? payment.getOrder().getId() : null);
        dto.setRazorpayOrderId(payment.getRazorpayOrderId());
        dto.setRazorpayPaymentId(payment.getRazorpayPaymentId());
        dto.setAmountPaise(payment.getAmountPaise());
        dto.setCurrency(payment.getCurrency());
        dto.setStatus(payment.getStatus());
        dto.setMethod(payment.getMethod());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }
}
