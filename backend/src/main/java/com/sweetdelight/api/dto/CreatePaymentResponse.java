package com.sweetdelight.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class CreatePaymentResponse {

    @Schema(description = "Razorpay Order ID", example = "order_O8xXXXXXXXXXXX")
    private String razorpayOrderId;

    @Schema(description = "Amount in paise (e.g. 59415 for INR 594.15)", example = "59415")
    private Long amountPaise;

    @Schema(description = "Currency", example = "INR")
    private String currency;

    @Schema(description = "Razorpay Key ID for client-side checkout", example = "rzp_test_XXXXXXXXXXXX")
    private String keyId;

    public CreatePaymentResponse() {}

    public CreatePaymentResponse(String razorpayOrderId, Long amountPaise, String currency, String keyId) {
        this.razorpayOrderId = razorpayOrderId;
        this.amountPaise = amountPaise;
        this.currency = currency;
        this.keyId = keyId;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public Long getAmountPaise() {
        return amountPaise;
    }

    public void setAmountPaise(Long amountPaise) {
        this.amountPaise = amountPaise;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
}
