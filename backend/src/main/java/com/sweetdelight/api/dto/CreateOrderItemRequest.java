package com.sweetdelight.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateOrderItemRequest {

    @Schema(description = "Product Variant ID", example = "1")
    @NotNull(message = "Variant ID is required")
    private Long variantId;

    @Schema(description = "Quantity ordered", example = "1")
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    public CreateOrderItemRequest() {}

    public CreateOrderItemRequest(Long variantId, Integer quantity) {
        this.variantId = variantId;
        this.quantity = quantity;
    }

    public Long getVariantId() {
        return variantId;
    }

    public void setVariantId(Long variantId) {
        this.variantId = variantId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
