package com.sweetdelight.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateVariantRequest {

    @Schema(description = "Variant title (e.g. 500g, 1kg, Box of 6)", example = "500g")
    @NotBlank(message = "Variant title is required")
    private String title;

    @Schema(description = "Price in INR", example = "699.00")
    @NotNull(message = "Variant price is required")
    @DecimalMin(value = "1.00", message = "Price must be at least 1.00 INR")
    private BigDecimal priceInr;

    @Schema(description = "Variant stock status", example = "true")
    private boolean inStock = true;

    public CreateVariantRequest() {}

    public CreateVariantRequest(String title, BigDecimal priceInr, boolean inStock) {
        this.title = title;
        this.priceInr = priceInr;
        this.inStock = inStock;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPriceInr() {
        return priceInr;
    }

    public void setPriceInr(BigDecimal priceInr) {
        this.priceInr = priceInr;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }
}
