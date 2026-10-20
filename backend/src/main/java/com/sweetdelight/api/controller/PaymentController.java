package com.sweetdelight.api.controller;

import com.sweetdelight.api.dto.CreatePaymentResponse;
import com.sweetdelight.api.dto.PaymentDto;
import com.sweetdelight.api.dto.VerifyPaymentRequest;
import com.sweetdelight.api.security.UserPrincipal;
import com.sweetdelight.api.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Payments", description = "Razorpay order creation, constant-time signature verification, and webhook processing")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Initialize Razorpay order for payment",
               description = "Creates a Razorpay order for the server-computed order total in paise. Only the order owner may invoke this, and only while status is PENDING_PAYMENT. Repeated calls reuse the existing unpaid Razorpay order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Razorpay order initialized or reused"),
            @ApiResponse(responseCode = "400", description = "Order is not in payable state or is COD"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Bearer token required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not order owner"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/orders/{id}/payment")
    public ResponseEntity<CreatePaymentResponse> createPaymentOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "Order ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.createOrGetPaymentOrder(id, principal.getId()));
    }

    @Operation(summary = "Verify Razorpay payment signature",
               description = "Accepts razorpay_order_id, razorpay_payment_id, and razorpay_signature. Verifies HMAC-SHA256 signature in constant time. On verification, marks payment PAID, transitions order to PLACED, records status event, and triggers customer + admin notifications. Idempotent: repeated calls do not trigger duplicate processing.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment verified successfully and order placed"),
            @ApiResponse(responseCode = "400", description = "Invalid signature or malformed payload"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Payment record not found")
    })
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/payments/verify")
    public ResponseEntity<PaymentDto> verifyPayment(@Valid @RequestBody VerifyPaymentRequest request) {
        return ResponseEntity.ok(paymentService.verifyPayment(request));
    }
}
