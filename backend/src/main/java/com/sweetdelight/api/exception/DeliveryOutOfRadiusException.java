package com.sweetdelight.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class DeliveryOutOfRadiusException extends RuntimeException {

    private final double distanceKm;
    private final double radiusKm;

    public DeliveryOutOfRadiusException(double distanceKm, double radiusKm) {
        super(String.format("Delivery address is outside our delivery radius of %.1f km (computed distance: %.2f km)", radiusKm, distanceKm));
        this.distanceKm = distanceKm;
        this.radiusKm = radiusKm;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public double getRadiusKm() {
        return radiusKm;
    }
}
