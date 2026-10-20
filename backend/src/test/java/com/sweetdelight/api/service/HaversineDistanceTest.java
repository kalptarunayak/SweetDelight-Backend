package com.sweetdelight.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HaversineDistanceTest {

    private HaversineDistanceService distanceService;

    @BeforeEach
    void setUp() {
        distanceService = new HaversineDistanceService();
    }

    @Test
    @DisplayName("Should return 0 km for identical coordinates")
    void testIdenticalCoordinates() {
        double distance = distanceService.calculateDistanceKm(19.0596, 72.8295, 19.0596, 72.8295);
        assertEquals(0.0, distance, 0.001);
    }

    @Test
    @DisplayName("Should accurately calculate distance within city radius (Bandra to Khar ~1.8 km)")
    void testWithinCityRadius() {
        // Bandra West (Shop) -> Khar West
        double distance = distanceService.calculateDistanceKm(19.0596, 72.8295, 19.0720, 72.8335);
        assertTrue(distance > 1.0 && distance < 2.5, "Distance should be around 1.4-1.8 km, got: " + distance);
    }

    @Test
    @DisplayName("Should accurately identify coordinates outside 5km radius (Bandra to Andheri ~8.5 km)")
    void testOutsideStoreRadius() {
        // Bandra West (Shop) -> Andheri West
        double distance = distanceService.calculateDistanceKm(19.0596, 72.8295, 19.1363, 72.8277);
        assertTrue(distance > 5.0, "Distance should be > 5.0 km, got: " + distance);
        assertTrue(distance < 12.0, "Distance should be < 12.0 km, got: " + distance);
    }

    @Test
    @DisplayName("Should calculate large inter-city distance (Mumbai to Pune ~120 km)")
    void testInterCityDistance() {
        double distance = distanceService.calculateDistanceKm(19.0596, 72.8295, 18.5204, 73.8567);
        assertTrue(distance > 100.0 && distance < 150.0, "Distance to Pune should be ~120 km, got: " + distance);
    }
}
