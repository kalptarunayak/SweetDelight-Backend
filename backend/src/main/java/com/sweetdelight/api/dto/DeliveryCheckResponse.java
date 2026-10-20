package com.sweetdelight.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class DeliveryCheckResponse {

    @Schema(description = "Whether the address is within the shop delivery radius", example = "true")
    private boolean deliverable;

    @Schema(description = "Computed distance in kilometers", example = "3.42")
    private double distanceKm;

    @Schema(description = "Current store delivery radius in kilometers", example = "5.0")
    private double radiusKm;

    @Schema(description = "Store latitude", example = "19.0596")
    private double shopLat;

    @Schema(description = "Store longitude", example = "72.8295")
    private double shopLng;

    @Schema(description = "Human-readable status message", example = "Address is within our delivery radius of 5.0 km")
    private String message;

    public DeliveryCheckResponse() {}

    public DeliveryCheckResponse(boolean deliverable, double distanceKm, double radiusKm, double shopLat, double shopLng, String message) {
        this.deliverable = deliverable;
        this.distanceKm = distanceKm;
        this.radiusKm = radiusKm;
        this.shopLat = shopLat;
        this.shopLng = shopLng;
        this.message = message;
    }

    public boolean isDeliverable() {
        return deliverable;
    }

    public void setDeliverable(boolean deliverable) {
        this.deliverable = deliverable;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public double getRadiusKm() {
        return radiusKm;
    }

    public void setRadiusKm(double radiusKm) {
        this.radiusKm = radiusKm;
    }

    public double getShopLat() {
        return shopLat;
    }

    public void setShopLat(double shopLat) {
        this.shopLat = shopLat;
    }

    public double getShopLng() {
        return shopLng;
    }

    public void setShopLng(double shopLng) {
        this.shopLng = shopLng;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
