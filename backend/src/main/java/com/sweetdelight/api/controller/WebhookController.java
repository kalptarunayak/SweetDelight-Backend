package com.sweetdelight.api.controller;

import com.sweetdelight.api.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
@Tag(name = "Payments", description = "Razorpay order creation, constant-time signature verification, and webhook processing")
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Operation(summary = "Razorpay Webhook receiver",
               description = "Public webhook endpoint for Razorpay events (payment.captured, order.paid, payment.failed). Authenticated via HMAC-SHA256 signature in X-Razorpay-Signature header. Idempotent: repeated events are safely ignored without duplicate notifications.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Webhook event processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid signature or malformed payload")
    })
    @PostMapping("/razorpay")
    public ResponseEntity<Map<String, String>> handleRazorpayWebhook(
            @RequestBody String rawPayload,
            @Parameter(description = "Hex HMAC-SHA256 signature from Razorpay", example = "4a58b88ef11612e61623910901e91280b27137a8581e7d0577fc761823ebaf82")
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signatureHeader) {
        webhookService.processWebhook(rawPayload, signatureHeader);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
