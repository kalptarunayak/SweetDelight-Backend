package com.sweetdelight.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class UpdateStockRequest {

    @Schema(description = "Stock availability flag", example = "true")
    @NotNull(message = "inStock status is required")
    private Boolean inStock;

    public UpdateStockRequest() {}

    public UpdateStockRequest(Boolean inStock) {
        this.inStock = inStock;
    }

    public Boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }
}
