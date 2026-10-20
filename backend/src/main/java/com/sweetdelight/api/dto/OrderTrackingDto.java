package com.sweetdelight.api.dto;

import com.sweetdelight.api.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderTrackingDto {

    @Schema(description = "Order ID", example = "1")
    private Long orderId;

    @Schema(description = "Order Number", example = "ORD-20261020-ABCD12")
    private String orderNumber;

    @Schema(description = "Current Order Status", example = "OUT_FOR_DELIVERY")
    private OrderStatus status;

    @Schema(description = "Estimated Delivery Time")
    private OffsetDateTime estimatedDeliveryTime;

    @Schema(description = "Rider Name", example = "Vikram Singh")
    private String riderName;

    @Schema(description = "Rider Phone", example = "+919876543299")
    private String riderPhone;

    @Schema(description = "Latest Rider Latitude", example = "19.0605")
    private Double riderLat;

    @Schema(description = "Latest Rider Longitude", example = "72.8310")
    private Double riderLng;

    @Schema(description = "Latest Rider Location Updated Timestamp")
    private OffsetDateTime riderLocationUpdatedAt;

    @Schema(description = "Order Status Timeline")
    private List<OrderStatusEventDto> timeline = new ArrayList<>();

    public OrderTrackingDto() {}

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
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

    public List<OrderStatusEventDto> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<OrderStatusEventDto> timeline) {
        this.timeline = timeline;
    }
}
