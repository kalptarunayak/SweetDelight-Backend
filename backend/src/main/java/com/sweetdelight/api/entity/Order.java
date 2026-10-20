package com.sweetdelight.api.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 64)
    private String orderNumber;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "customer_name", nullable = false, length = 255)
    private String customerName;

    @Column(name = "whatsapp_number", nullable = false, length = 30)
    private String whatsappNumber;

    @Column(name = "delivery_address", nullable = false, columnDefinition = "TEXT")
    private String deliveryAddress;

    @Column(name = "delivery_lat", nullable = false)
    private Double deliveryLat;

    @Column(name = "delivery_lng", nullable = false)
    private Double deliveryLng;

    @Column(name = "delivery_distance_km", nullable = false)
    private Double deliveryDistanceKm;

    @Column(name = "delivery_slot", nullable = false, length = 100)
    private String deliverySlot;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Column(name = "subtotal_inr", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotalInr;

    @Column(name = "discount_inr", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountInr = BigDecimal.ZERO;

    @Column(name = "coupon_code", length = 50)
    private String couponCode;

    @Column(name = "total_inr", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalInr;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OrderStatus status;

    @Column(name = "estimated_delivery_time")
    private OffsetDateTime estimatedDeliveryTime;

    @Column(name = "rider_name", length = 100)
    private String riderName;

    @Column(name = "rider_phone", length = 50)
    private String riderPhone;

    @Column(name = "rider_lat")
    private Double riderLat;

    @Column(name = "rider_lng")
    private Double riderLng;

    @Column(name = "rider_location_updated_at")
    private OffsetDateTime riderLocationUpdatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<OrderStatusEvent> statusEvents = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = OffsetDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void addStatusEvent(OrderStatusEvent event) {
        statusEvents.add(event);
        event.setOrder(this);
    }

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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public List<OrderStatusEvent> getStatusEvents() {
        return statusEvents;
    }

    public void setStatusEvents(List<OrderStatusEvent> statusEvents) {
        this.statusEvents = statusEvents;
    }
}
