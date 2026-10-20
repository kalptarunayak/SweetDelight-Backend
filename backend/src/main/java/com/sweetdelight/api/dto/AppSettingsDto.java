package com.sweetdelight.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public class AppSettingsDto {

    @Schema(description = "Settings ID", example = "1")
    private Long id;

    @Schema(description = "Store latitude", example = "19.0596")
    private Double shopLat;

    @Schema(description = "Store longitude", example = "72.8295")
    private Double shopLng;

    @Schema(description = "Delivery radius in kilometers", example = "5.0")
    private Double deliveryRadiusKm;

    @Schema(description = "Last update timestamp")
    private OffsetDateTime updatedAt;

    public AppSettingsDto() {}

    public AppSettingsDto(Long id, Double shopLat, Double shopLng, Double deliveryRadiusKm, OffsetDateTime updatedAt) {
        this.id = id;
        this.shopLat = shopLat;
        this.shopLng = shopLng;
        this.deliveryRadiusKm = deliveryRadiusKm;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getShopLat() {
        return shopLat;
    }

    public void setShopLat(Double shopLat) {
        this.shopLat = shopLat;
    }

    public Double getShopLng() {
        return shopLng;
    }

    public void setShopLng(Double shopLng) {
        this.shopLng = shopLng;
    }

    public Double getDeliveryRadiusKm() {
        return deliveryRadiusKm;
    }

    public void setDeliveryRadiusKm(Double deliveryRadiusKm) {
        this.deliveryRadiusKm = deliveryRadiusKm;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
