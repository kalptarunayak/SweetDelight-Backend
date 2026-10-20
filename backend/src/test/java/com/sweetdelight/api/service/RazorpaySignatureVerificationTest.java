package com.sweetdelight.api.service;

import com.sweetdelight.api.config.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RazorpaySignatureVerificationTest {

    private RazorpayService razorpayService;
    private AppProperties appProperties;

    private static final String TEST_KEY_SECRET = "super_secret_key_1234567890";
    private static final String TEST_WEBHOOK_SECRET = "webhook_secret_abcdef12345";

    @BeforeEach
    void setUp() {
        appProperties = new AppProperties();
        appProperties.getRazorpay().setKeySecret(TEST_KEY_SECRET);
        appProperties.getRazorpay().setWebhookSecret(TEST_WEBHOOK_SECRET);
        razorpayService = new RazorpayService(appProperties);
    }

    @Test
    @DisplayName("Should verify authentic payment signature as TRUE")
    void testValidPaymentSignature() {
        String orderId = "order_O8xTestOrderId123";
        String paymentId = "pay_O8yTestPaymentId456";
        String data = orderId + "|" + paymentId;

        // Generate genuine signature
        String validSignature = RazorpayService.calculateHmacSha256Hex(data, TEST_KEY_SECRET);

        boolean result = razorpayService.verifyPaymentSignature(orderId, paymentId, validSignature);
        assertTrue(result, "Valid signature must verify successfully");
    }

    @Test
    @DisplayName("Should reject tampered payment signature as FALSE")
    void testTamperedPaymentSignature() {
        String orderId = "order_O8xTestOrderId123";
        String paymentId = "pay_O8yTestPaymentId456";
        String data = orderId + "|" + paymentId;

        String validSignature = RazorpayService.calculateHmacSha256Hex(data, TEST_KEY_SECRET);
        // Tamper with the last character
        String tamperedSignature = validSignature.substring(0, validSignature.length() - 1) + "0";

        boolean result = razorpayService.verifyPaymentSignature(orderId, paymentId, tamperedSignature);
        assertFalse(result, "Tampered signature must be rejected");
    }

    @Test
    @DisplayName("Should reject valid signature if orderId or paymentId is modified")
    void testTamperedOrderIdOrPaymentId() {
        String orderId = "order_O8xTestOrderId123";
        String paymentId = "pay_O8yTestPaymentId456";
        String data = orderId + "|" + paymentId;

        String validSignature = RazorpayService.calculateHmacSha256Hex(data, TEST_KEY_SECRET);

        // Modifying orderId
        boolean result1 = razorpayService.verifyPaymentSignature(orderId + "_tampered", paymentId, validSignature);
        assertFalse(result1, "Modified orderId must fail verification");

        // Modifying paymentId
        boolean result2 = razorpayService.verifyPaymentSignature(orderId, paymentId + "_tampered", validSignature);
        assertFalse(result2, "Modified paymentId must fail verification");
    }

    @Test
    @DisplayName("Should verify authentic webhook signature as TRUE")
    void testValidWebhookSignature() {
        String rawBody = "{\"event\":\"payment.captured\",\"payload\":{\"payment\":{\"entity\":{\"id\":\"pay_123\"}}}}";
        String validSignature = RazorpayService.calculateHmacSha256Hex(rawBody, TEST_WEBHOOK_SECRET);

        boolean result = razorpayService.verifyWebhookSignature(rawBody, validSignature);
        assertTrue(result, "Valid webhook signature must verify successfully");
    }

    @Test
    @DisplayName("Should reject tampered webhook body or signature as FALSE")
    void testTamperedWebhookPayloadOrSignature() {
        String rawBody = "{\"event\":\"payment.captured\",\"amount\":50000}";
        String validSignature = RazorpayService.calculateHmacSha256Hex(rawBody, TEST_WEBHOOK_SECRET);

        // Tampering payload
        String tamperedBody = "{\"event\":\"payment.captured\",\"amount\":10000}";
        boolean result1 = razorpayService.verifyWebhookSignature(tamperedBody, validSignature);
        assertFalse(result1, "Tampered payload must fail webhook signature check");

        // Tampering signature
        boolean result2 = razorpayService.verifyWebhookSignature(rawBody, "invalid_webhook_signature");
        assertFalse(result2, "Invalid signature header must fail check");
    }
}
