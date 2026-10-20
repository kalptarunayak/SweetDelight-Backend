package com.sweetdelight.api.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "variant_id", nullable = false)
    private Long variantId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "variant_title", nullable = false)
    private String variantTitle;

    @Column(name = "unit_price_inr", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPriceInr;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "total_price_inr", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPriceInr;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
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
