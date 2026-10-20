package com.sweetdelight.api.service;

import com.razorpay.RazorpayClient;
import com.sweetdelight.api.config.AppProperties;
import com.sweetdelight.api.exception.BadRequestException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

@Service
public class RazorpayService {

    private static final Logger log = LoggerFactory.getLogger(RazorpayService.class);
    private final AppProperties appProperties;

    public RazorpayService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    /**
     * Creates an order with Razorpay or test fallback if keys are placeholders.
     */
    public String createRazorpayOrder(long amountPaise, String currency, String receipt) {
        String keyId = appProperties.getRazorpay().getKeyId();
        String keySecret = appProperties.getRazorpay().getKeySecret();

        // If credentials are non-placeholder, invoke the official Razorpay SDK
        if (keyId != null && !keyId.contains("placeholder") && keySecret != null && !keySecret.contains("placeholder")) {
            try {
                RazorpayClient client = new RazorpayClient(keyId, keySecret);
                JSONObject orderRequest = new JSONObject();
                orderRequest.put("amount", amountPaise);
                orderRequest.put("currency", currency);
                orderRequest.put("receipt", receipt);
                orderRequest.put("payment_capture", 1);

                com.razorpay.Order order = client.orders.create(orderRequest);
                String orderId = order.get("id");
                log.info("Created Razorpay order: {} for receipt: {}", maskId(orderId), receipt);
                return orderId;
            } catch (Exception e) {
                log.error("Razorpay API order creation failed: {}", e.getMessage());
                throw new BadRequestException("Failed to initialize payment gateway order: " + e.getMessage());
            }
        }

        // Test/sandbox fallback mode when keys are not yet configured in environment
        String testOrderId = "order_test_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
        log.info("Generated sandbox Razorpay order ID: {} for amount: {} paise", maskId(testOrderId), amountPaise);
        return testOrderId;
    }

    /**
     * Verifies payment signature using HMAC-SHA256 with key secret in constant time.
     * Expected string: razorpayOrderId + "|" + razorpayPaymentId
     */
    public boolean verifyPaymentSignature(String razorpayOrderId, String razorpayPaymentId, String signature) {
        if (razorpayOrderId == null || razorpayPaymentId == null || signature == null) {
            return false;
        }

        String secret = appProperties.getRazorpay().getKeySecret();
        String data = razorpayOrderId + "|" + razorpayPaymentId;
        return verifyHmacSha256Hex(data, signature, secret);
    }

    /**
     * Verifies webhook signature against raw request body using constant time comparison.
     */
    public boolean verifyWebhookSignature(String rawBody, String signatureHeader) {
        if (rawBody == null || signatureHeader == null) {
            return false;
        }

        String secret = appProperties.getRazorpay().getWebhookSecret();
        return verifyHmacSha256Hex(rawBody, signatureHeader, secret);
    }

    /**
     * Constant-time HMAC-SHA256 hexadecimal signature verification.
     */
    public boolean verifyHmacSha256Hex(String data, String signatureHex, String secret) {
        if (data == null || signatureHex == null || secret == null) {
            return false;
        }

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            String expectedHex = bytesToHex(hash);

            // Constant-time comparison to prevent timing attacks
            return MessageDigest.isEqual(
                    expectedHex.toLowerCase().getBytes(StandardCharsets.UTF_8),
                    signatureHex.trim().toLowerCase().getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            log.error("Error computing HMAC-SHA256 signature: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Calls Razorpay refund API for full refund.
     */
    public String refundPayment(String paymentId, long amountPaise) {
        String keyId = appProperties.getRazorpay().getKeyId();
        String keySecret = appProperties.getRazorpay().getKeySecret();

        if (keyId != null && !keyId.contains("placeholder") && keySecret != null && !keySecret.contains("placeholder")) {
            try {
                RazorpayClient client = new RazorpayClient(keyId, keySecret);
                JSONObject refundRequest = new JSONObject();
                refundRequest.put("amount", amountPaise);
                com.razorpay.Refund refund = client.payments.refund(paymentId, refundRequest);
                String refundId = refund.get("id");
                log.info("Processed Razorpay refund: {} for payment: {}", maskId(refundId), maskId(paymentId));
                return refundId;
            } catch (Exception e) {
                log.error("Razorpay refund API call failed: {}", e.getMessage());
                throw new BadRequestException("Refund processing failed: " + e.getMessage());
            }
        }

        String mockRefundId = "rfnd_test_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
        log.info("Processed simulated refund: {} for payment: {}", maskId(mockRefundId), maskId(paymentId));
        return mockRefundId;
    }

    /**
     * Utility to calculate HMAC-SHA256 hex string (useful for generating test signatures in tests).
     */
    public static String calculateHmacSha256Hex(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("HMAC computation failed", e);
        }
    }

    public static String maskId(String id) {
        if (id == null) return "null";
        if (id.length() <= 8) return id;
        return id.substring(0, 4) + "***" + id.substring(id.length() - 4);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
