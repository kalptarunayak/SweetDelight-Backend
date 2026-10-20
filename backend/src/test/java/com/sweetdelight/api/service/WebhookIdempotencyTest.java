package com.sweetdelight.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetdelight.api.entity.*;
import com.sweetdelight.api.repository.OrderRepository;
import com.sweetdelight.api.repository.OrderStatusEventRepository;
import com.sweetdelight.api.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookIdempotencyTest {

    @Mock
    private RazorpayService razorpayService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderStatusEventRepository statusEventRepository;

    @Mock
    private NotificationService notificationService;

    private WebhookService webhookService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        webhookService = new WebhookService(
                razorpayService,
                paymentRepository,
                orderRepository,
                statusEventRepository,
                notificationService,
                objectMapper
        );
    }

    @Test
    @DisplayName("Webhook Idempotency: receiving identical payment.captured twice should trigger notifications ONLY ONCE")
    void testWebhookIdempotency() {
        String razorpayOrderId = "order_O8xIdempotent123";
        String razorpayPaymentId = "pay_O8yIdempotent456";
        String signature = "valid_signature_hash";

        String rawPayload = "{\n" +
                "  \"entity\": \"event\",\n" +
                "  \"event\": \"payment.captured\",\n" +
                "  \"payload\": {\n" +
                "    \"payment\": {\n" +
                "      \"entity\": {\n" +
                "        \"id\": \"" + razorpayPaymentId + "\",\n" +
                "        \"order_id\": \"" + razorpayOrderId + "\",\n" +
                "        \"method\": \"upi\",\n" +
                "        \"amount\": 69900,\n" +
                "        \"status\": \"captured\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        when(razorpayService.verifyWebhookSignature(eq(rawPayload), eq(signature))).thenReturn(true);

        Order order = new Order();
        order.setId(101L);
        order.setOrderNumber("ORD-20261020-001");
        order.setCustomerId(UUID.randomUUID());
        order.setCustomerName("Aarav Sharma");
        order.setWhatsappNumber("+919876543210");
        order.setDeliverySlot("Today 5:00 PM");
        order.setTotalInr(new BigDecimal("699.00"));
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        Payment payment = new Payment();
        payment.setId(501L);
        payment.setOrder(order);
        payment.setRazorpayOrderId(razorpayOrderId);
        payment.setAmountPaise(69900L);
        payment.setStatus(PaymentStatus.CREATED);

        when(paymentRepository.findByRazorpayOrderId(razorpayOrderId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 1st Webhook Execution
        webhookService.processWebhook(rawPayload, signature);

        assertEquals(PaymentStatus.PAID, payment.getStatus());
        assertEquals(OrderStatus.PLACED, order.getStatus());
        assertEquals(razorpayPaymentId, payment.getRazorpayPaymentId());

        // Verify notification sent once
        verify(notificationService, times(1)).notifyPaymentConfirmed(eq(order), eq(payment));
        verify(orderRepository, times(1)).save(order);
        verify(statusEventRepository, times(1)).save(any(OrderStatusEvent.class));

        // 2nd Webhook Execution with identical payload (Duplicate event from Razorpay)
        webhookService.processWebhook(rawPayload, signature);

        // Notification MUST NOT be sent a second time (still exactly 1 invocation)
        verify(notificationService, times(1)).notifyPaymentConfirmed(any(), any());
        // Order save was not called again
        verify(orderRepository, times(1)).save(order);
    }
}
