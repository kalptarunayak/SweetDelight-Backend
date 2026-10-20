package com.sweetdelight.api.dto;

import com.sweetdelight.api.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public class UpdateOrderStatusRequest {

    @Schema(description = "New Order Status", example = "CONFIRMED")
    @NotNull(message = "Order status is required")
    private OrderStatus status;

    @Schema(description = "Optional estimated delivery time (e.g. for OUT_FOR_DELIVERY)")
    private OffsetDateTime estimatedDeliveryTime;

    @Schema(description = "Optional rider name", example = "Vikram Singh")
    private String riderName;

    @Schema(description = "Optional rider phone", example = "+919876543299")
    private String riderPhone;

    @Schema(description = "Optional note or remark for this status transition", example = "Cake is in the oven, decorating shortly.")
    private String notes;

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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
