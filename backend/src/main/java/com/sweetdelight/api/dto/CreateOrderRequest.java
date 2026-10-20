package com.sweetdelight.api.dto;

import com.sweetdelight.api.entity.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

public class CreateOrderRequest {

    @Schema(description = "Customer full name", example = "Aarav Sharma")
    @NotBlank(message = "Customer name is required")
    private String customerName;

    @Schema(description = "WhatsApp contact number in E.164 format", example = "+919876543210")
    @NotBlank(message = "WhatsApp number is required")
    @Pattern(regexp = "^\\+[1-9]\\d{6,14}$", message = "WhatsApp number must be in E.164 format (e.g. +919876543210)")
    private String whatsappNumber;

    @Schema(description = "Full delivery street address", example = "Flat 402, Sea Green Apts, Hill Road, Bandra West, Mumbai 400050")
    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    @Schema(description = "Delivery location latitude", example = "19.0620")
    @NotNull(message = "Delivery latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double deliveryLat;

    @Schema(description = "Delivery location longitude", example = "72.8350")
    @NotNull(message = "Delivery longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double deliveryLng;

    @Schema(description = "Requested delivery time slot", example = "Today 4:00 PM - 6:00 PM")
    @NotBlank(message = "Delivery slot is required")
    private String deliverySlot;

    @Schema(description = "Optional baking/delivery instructions or cake message", example = "Please write 'Happy 30th Birthday Rahul!' in dark chocolate")
    private String note;

    @Schema(description = "Payment method: ONLINE or COD", example = "ONLINE")
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Schema(description = "Optional promotional coupon code", example = "SWEET15")
    private String couponCode;

    @Schema(description = "List of ordered items (variantId and quantity)")
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<CreateOrderItemRequest> items = new ArrayList<>();

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getWhatsappNumber() {
        return whatsappNumber;
    }

    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Double getDeliveryLat() {
        return deliveryLat;
    }

    public void setDeliveryLat(Double deliveryLat) {
        this.deliveryLat = deliveryLat;
    }

    public Double getDeliveryLng() {
        return deliveryLng;
    }

    public void setDeliveryLng(Double deliveryLng) {
        this.deliveryLng = deliveryLng;
    }

    public String getDeliverySlot() {
        return deliverySlot;
    }

    public void setDeliverySlot(String deliverySlot) {
        this.deliverySlot = deliverySlot;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public List<CreateOrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CreateOrderItemRequest> items) {
        this.items = items;
    }
}
