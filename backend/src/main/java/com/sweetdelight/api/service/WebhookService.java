package com.sweetdelight.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetdelight.api.entity.Order;
import com.sweetdelight.api.entity.OrderStatus;
import com.sweetdelight.api.entity.OrderStatusEvent;
import com.sweetdelight.api.entity.Payment;
import com.sweetdelight.api.entity.PaymentStatus;
import com.sweetdelight.api.exception.BadRequestException;
import com.sweetdelight.api.repository.OrderRepository;
import com.sweetdelight.api.repository.OrderStatusEventRepository;
import com.sweetdelight.api.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final RazorpayService razorpayService;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusEventRepository statusEventRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public WebhookService(RazorpayService razorpayService,
                          PaymentRepository paymentRepository,
                          OrderRepository orderRepository,
                          OrderStatusEventRepository statusEventRepository,
                          NotificationService notificationService,
                          ObjectMapper objectMapper) {
        this.razorpayService = razorpayService;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.statusEventRepository = statusEventRepository;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    /**
     * Processes Razorpay webhook with signature verification and idempotent handling.
     */
    @Transactional
    public void processWebhook(String rawPayload, String signatureHeader) {
        // Verify X-Razorpay-Signature header against raw body
        boolean isValid = razorpayService.verifyWebhookSignature(rawPayload, signatureHeader);
        if (!isValid) {
            log.warn("Invalid webhook signature received from Razorpay");
            throw new BadRequestException("Invalid webhook signature");
        }

        try {
            JsonNode root = objectMapper.readTree(rawPayload);
            String eventType = root.has("event") ? root.get("event").asText() : "";
            log.info("Received verified Razorpay webhook event: {}", eventType);

            if ("payment.captured".equals(eventType) || "order.paid".equals(eventType)) {
                handlePaymentCaptured(root, rawPayload);
            } else if ("payment.failed".equals(eventType)) {
                handlePaymentFailed(root, rawPayload);
            } else {
                log.info("Ignored unhandled Razorpay event: {}", eventType);
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error parsing Razorpay webhook payload: {}", e.getMessage(), e);
            throw new BadRequestException("Malformed webhook payload");
        }
    }

    private void handlePaymentCaptured(JsonNode root, String rawPayload) {
        JsonNode payloadNode = root.path("payload");
        JsonNode paymentEntity = payloadNode.path("payment").path("entity");
        JsonNode orderEntity = payloadNode.path("order").path("entity");

        String razorpayOrderId = null;
        if (!orderEntity.isMissingNode() && orderEntity.has("id")) {
            razorpayOrderId = orderEntity.get("id").asText();
        } else if (!paymentEntity.isMissingNode() && paymentEntity.has("order_id")) {
            razorpayOrderId = paymentEntity.get("order_id").asText();
        }

        if (razorpayOrderId == null || razorpayOrderId.isBlank()) {
            log.warn("Webhook payload does not contain order_id");
            return;
        }

        Optional<Payment> paymentOpt = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
        if (paymentOpt.isEmpty()) {
            log.warn("Payment record not found for Razorpay order ID: {}", RazorpayService.maskId(razorpayOrderId));
            return;
        }

        Payment payment = paymentOpt.get();

        // Idempotency: if already PAID, exit without re-processing or duplicate notifications
        if (payment.getStatus() == PaymentStatus.PAID) {
            log.info("Payment {} is already PAID. Webhook handled idempotently.",
                    RazorpayService.maskId(razorpayOrderId));
            return;
        }

        String paymentId = paymentEntity.has("id") ? paymentEntity.get("id").asText() : null;
        String method = paymentEntity.has("method") ? paymentEntity.get("method").asText() : null;

        payment.setRazorpayPaymentId(paymentId);
        payment.setMethod(method);
        payment.setStatus(PaymentStatus.PAID);
        payment.setRawEventJson(rawPayload);
        Payment savedPayment = paymentRepository.save(payment);

        Order order = payment.getOrder();
        if (order.getStatus() == OrderStatus.PENDING_PAYMENT || order.getStatus() == OrderStatus.PAYMENT_FAILED) {
            order.setStatus(OrderStatus.PLACED);
            orderRepository.save(order);

            OrderStatusEvent event = new OrderStatusEvent(order, OrderStatus.PLACED,
                    "Payment captured via webhook. Razorpay Payment ID: " + RazorpayService.maskId(paymentId));
            statusEventRepository.save(event);

            log.info("Order {} transitioned to PLACED via webhook", order.getOrderNumber());

            // Trigger payment-confirmed notifications once
            notificationService.notifyPaymentConfirmed(order, savedPayment);
        }
    }

    private void handlePaymentFailed(JsonNode root, String rawPayload) {
        JsonNode payloadNode = root.path("payload");
        JsonNode paymentEntity = payloadNode.path("payment").path("entity");

        String razorpayOrderId = paymentEntity.has("order_id") ? paymentEntity.get("order_id").asText() : null;
        String paymentId = paymentEntity.has("id") ? paymentEntity.get("id").asText() : null;

        if (razorpayOrderId == null) {
            return;
        }

        Optional<Payment> paymentOpt = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
        if (paymentOpt.isEmpty()) {
            return;
        }

        Payment payment = paymentOpt.get();
        // If it was already PAID, don't revert to failed
        if (payment.getStatus() == PaymentStatus.PAID) {
            return;
        }

        payment.setRazorpayPaymentId(paymentId);
        payment.setStatus(PaymentStatus.FAILED);
        payment.setRawEventJson(rawPayload);
        paymentRepository.save(payment);

        Order order = payment.getOrder();
        order.setStatus(OrderStatus.PAYMENT_FAILED);
        orderRepository.save(order);

        OrderStatusEvent event = new OrderStatusEvent(order, OrderStatus.PAYMENT_FAILED,
                "Payment failed via webhook. Razorpay Payment ID: " + RazorpayService.maskId(paymentId));
        statusEventRepository.save(event);

        String retryUrl = "/api/orders/" + order.getId() + "/payment";
        notificationService.notifyPaymentFailed(order, retryUrl);
    }
}
