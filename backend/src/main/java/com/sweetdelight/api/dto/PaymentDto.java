package com.sweetdelight.api.dto;

import com.sweetdelight.api.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public class PaymentDto {

    @Schema(description = "Payment record ID", example = "1")
    private Long id;

    @Schema(description = "Order ID", example = "1")
    private Long orderId;

    @Schema(description = "Razorpay Order ID", example = "order_O8xXXXXXXXXXXX")
    private String razorpayOrderId;

    @Schema(description = "Razorpay Payment ID", example = "pay_O8yYYYYYYYYYYY")
    private String razorpayPaymentId;

    @Schema(description = "Amount in paise", example = "59415")
    private Long amountPaise;

    @Schema(description = "Currency", example = "INR")
    private String currency;

    @Schema(description = "Payment Status", example = "PAID")
    private PaymentStatus status;

    @Schema(description = "Payment Method (card, upi, netbanking, etc.)", example = "upi")
    private String method;

    @Schema(description = "Created Timestamp")
    private OffsetDateTime createdAt;

    public PaymentDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
