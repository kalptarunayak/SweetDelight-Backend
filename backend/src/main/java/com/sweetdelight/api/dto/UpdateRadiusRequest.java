package com.sweetdelight.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class UpdateRadiusRequest {

    @Schema(description = "Delivery radius in kilometers", example = "7.5")
    @NotNull(message = "Delivery radius is required")
    @DecimalMin(value = "0.5", message = "Delivery radius must be at least 0.5 km")
    @DecimalMax(value = "100.0", message = "Delivery radius cannot exceed 100 km")
    private Double deliveryRadiusKm;

    @Schema(description = "Optional updated shop latitude", example = "19.0596")
    private Double shopLat;

    @Schema(description = "Optional updated shop longitude", example = "72.8295")
    private Double shopLng;

    public Double getDeliveryRadiusKm() {
        return deliveryRadiusKm;
    }

    public void setDeliveryRadiusKm(Double deliveryRadiusKm) {
        this.deliveryRadiusKm = deliveryRadiusKm;
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
}
