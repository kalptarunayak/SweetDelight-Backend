package com.sweetdelight.api.dto;

import com.sweetdelight.api.entity.OrderStatus;
import com.sweetdelight.api.entity.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderDto {

    @Schema(description = "Order ID", example = "1")
    private Long id;

    @Schema(description = "Public Order Number", example = "ORD-20261020-ABCD12")
    private String orderNumber;

    @Schema(description = "Customer UUID")
    private UUID customerId;

    @Schema(description = "Customer Name", example = "Aarav Sharma")
    private String customerName;

    @Schema(description = "WhatsApp contact number", example = "+919876543210")
    private String whatsappNumber;

    @Schema(description = "Delivery address", example = "Flat 402, Sea Green Apts, Hill Road, Bandra West, Mumbai")
    private String deliveryAddress;

    @Schema(description = "Delivery latitude", example = "19.0620")
    private Double deliveryLat;

    @Schema(description = "Delivery longitude", example = "72.8350")
    private Double deliveryLng;

    @Schema(description = "Delivery distance from shop in km", example = "3.42")
    private Double deliveryDistanceKm;

    @Schema(description = "Delivery slot", example = "Today 4:00 PM - 6:00 PM")
    private String deliverySlot;

    @Schema(description = "Order notes / cake message", example = "Happy Birthday Rahul!")
    private String note;

    @Schema(description = "Payment method", example = "ONLINE")
    private PaymentMethod paymentMethod;

    @Schema(description = "Subtotal in INR", example = "699.00")
    private BigDecimal subtotalInr;

    @Schema(description = "Discount in INR", example = "104.85")
    private BigDecimal discountInr;

    @Schema(description = "Coupon code applied", example = "SWEET15")
    private String couponCode;

    @Schema(description = "Total payable in INR", example = "594.15")
    private BigDecimal totalInr;

    @Schema(description = "Order status", example = "PLACED")
    private OrderStatus status;

    @Schema(description = "Estimated delivery timestamp")
    private OffsetDateTime estimatedDeliveryTime;

    @Schema(description = "Assigned rider name", example = "Vikram Singh")
    private String riderName;

    @Schema(description = "Assigned rider phone", example = "+919876543299")
    private String riderPhone;

    @Schema(description = "Latest rider latitude", example = "19.0605")
    private Double riderLat;

    @Schema(description = "Latest rider longitude", example = "72.8310")
    private Double riderLng;

    @Schema(description = "Latest rider location timestamp")
    private OffsetDateTime riderLocationUpdatedAt;

    @Schema(description = "Order items")
    private List<OrderItemDto> items = new ArrayList<>();

    @Schema(description = "Status change history")
    private List<OrderStatusEventDto> statusEvents = new ArrayList<>();

    @Schema(description = "Creation timestamp")
    private OffsetDateTime createdAt;

    public OrderDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

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

    public Double getDeliveryDistanceKm() {
        return deliveryDistanceKm;
    }

    public void setDeliveryDistanceKm(Double deliveryDistanceKm) {
        this.deliveryDistanceKm = deliveryDistanceKm;
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

    public BigDecimal getSubtotalInr() {
        return subtotalInr;
    }

    public void setSubtotalInr(BigDecimal subtotalInr) {
        this.subtotalInr = subtotalInr;
    }

    public BigDecimal getDiscountInr() {
        return discountInr;
    }

    public void setDiscountInr(BigDecimal discountInr) {
        this.discountInr = discountInr;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public BigDecimal getTotalInr() {
        return totalInr;
    }

    public void setTotalInr(BigDecimal totalInr) {
        this.totalInr = totalInr;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public OffsetDateTime getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(OffsetDateTime estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public String getRiderPhone() {
        return riderPhone;
    }

    public void setRiderPhone(String riderPhone) {
        this.riderPhone = riderPhone;
    }

    public Double getRiderLat() {
        return riderLat;
    }

    public void setRiderLat(Double riderLat) {
        this.riderLat = riderLat;
    }

    public Double getRiderLng() {
        return riderLng;
    }

    public void setRiderLng(Double riderLng) {
        this.riderLng = riderLng;
    }

    public OffsetDateTime getRiderLocationUpdatedAt() {
        return riderLocationUpdatedAt;
    }

    public void setRiderLocationUpdatedAt(OffsetDateTime riderLocationUpdatedAt) {
        this.riderLocationUpdatedAt = riderLocationUpdatedAt;
    }

    public List<OrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDto> items) {
        this.items = items;
    }

    public List<OrderStatusEventDto> getStatusEvents() {
        return statusEvents;
    }

    public void setStatusEvents(List<OrderStatusEventDto> statusEvents) {
        this.statusEvents = statusEvents;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
