package com.sweetdelight.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public class OrderItemDto {

    @Schema(description = "Item ID", example = "1")
    private Long id;

    @Schema(description = "Product ID", example = "1")
    private Long productId;

    @Schema(description = "Product Variant ID", example = "1")
    private Long variantId;

    @Schema(description = "Product Name", example = "Belgian Chocolate Truffle Cake")
    private String productName;

    @Schema(description = "Variant Title", example = "500g")
    private String variantTitle;

    @Schema(description = "Unit price in INR", example = "699.00")
    private BigDecimal unitPriceInr;

    @Schema(description = "Quantity ordered", example = "1")
    private Integer quantity;

    @Schema(description = "Total price in INR", example = "699.00")
    private BigDecimal totalPriceInr;

    public OrderItemDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getVariantId() {
        return variantId;
    }

    public void setVariantId(Long variantId) {
        this.variantId = variantId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getVariantTitle() {
        return variantTitle;
    }

    public void setVariantTitle(String variantTitle) {
        this.variantTitle = variantTitle;
    }

    public BigDecimal getUnitPriceInr() {
        return unitPriceInr;
    }

    public void setUnitPriceInr(BigDecimal unitPriceInr) {
        this.unitPriceInr = unitPriceInr;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPriceInr() {
        return totalPriceInr;
    }

    public void setTotalPriceInr(BigDecimal totalPriceInr) {
        this.totalPriceInr = totalPriceInr;
    }
}
