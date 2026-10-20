package com.sweetdelight.api.dto;

import com.sweetdelight.api.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public class OrderStatusEventDto {

    @Schema(description = "Event ID", example = "1")
    private Long id;

    @Schema(description = "Order Status", example = "PLACED")
    private OrderStatus status;

    @Schema(description = "Notes or remarks", example = "Order placed successfully")
    private String notes;

    @Schema(description = "Timestamp of the status change")
    private OffsetDateTime createdAt;

    public OrderStatusEventDto() {}

    public OrderStatusEventDto(Long id, OrderStatus status, String notes, OffsetDateTime createdAt) {
        this.id = id;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
