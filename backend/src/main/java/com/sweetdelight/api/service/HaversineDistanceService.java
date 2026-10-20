package com.sweetdelight.api.service;

import org.springframework.stereotype.Service;

@Service
public class HaversineDistanceService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Computes the Great-Circle distance between two coordinates in kilometers using the Haversine formula.
     *
     * @param lat1 Latitude of point 1 in degrees
     * @param lon1 Longitude of point 1 in degrees
     * @param lat2 Latitude of point 2 in degrees
     * @param lon2 Longitude of point 2 in degrees
     * @return Distance in kilometers
     */
    public double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2.0) * Math.sin(dLat / 2.0)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2.0) * Math.sin(dLon / 2.0);

        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));

        double distance = EARTH_RADIUS_KM * c;
        // Round to 2 decimal places
        return Math.round(distance * 100.0) / 100.0;
    }
}
