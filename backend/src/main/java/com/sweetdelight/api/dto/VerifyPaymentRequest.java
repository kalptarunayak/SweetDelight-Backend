package com.sweetdelight.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class VerifyPaymentRequest {

    @Schema(description = "Razorpay Order ID", example = "order_O8xXXXXXXXXXXX")
    @NotBlank(message = "razorpay_order_id is required")
    @JsonProperty("razorpay_order_id")
    @JsonAlias({"razorpayOrderId"})
    private String razorpayOrderId;

    @Schema(description = "Razorpay Payment ID", example = "pay_O8yYYYYYYYYYYY")
    @NotBlank(message = "razorpay_payment_id is required")
    @JsonProperty("razorpay_payment_id")
    @JsonAlias({"razorpayPaymentId"})
    private String razorpayPaymentId;

    @Schema(description = "Razorpay Signature (HMAC-SHA256)", example = "4a58b88ef11612e61623910901e91280b27137a8581e7d0577fc761823ebaf82")
    @NotBlank(message = "razorpay_signature is required")
    @JsonProperty("razorpay_signature")
    @JsonAlias({"razorpaySignature"})
    private String razorpaySignature;

    public VerifyPaymentRequest() {}

    public VerifyPaymentRequest(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        this.razorpayOrderId = razorpayOrderId;
        this.razorpayPaymentId = razorpayPaymentId;
        this.razorpaySignature = razorpaySignature;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public String getRazorpaySignature() {
        return razorpaySignature;
    }

    public void setRazorpaySignature(String razorpaySignature) {
        this.razorpaySignature = razorpaySignature;
    }
}
