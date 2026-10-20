package com.sweetdelight.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public class ProductVariantDto {

    @Schema(description = "Variant ID", example = "1")
    private Long id;

    @Schema(description = "Variant title (weight/size)", example = "500g")
    private String title;

    @Schema(description = "Price in INR", example = "699.00")
    private BigDecimal priceInr;

    @Schema(description = "Whether the variant is in stock", example = "true")
    private boolean inStock;

    public ProductVariantDto() {}

    public ProductVariantDto(Long id, String title, BigDecimal priceInr, boolean inStock) {
        this.id = id;
        this.title = title;
        this.priceInr = priceInr;
        this.inStock = inStock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
