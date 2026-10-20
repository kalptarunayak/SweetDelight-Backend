package com.sweetdelight.api.service;

import com.sweetdelight.api.dto.AppSettingsDto;
import com.sweetdelight.api.dto.DeliveryCheckRequest;
import com.sweetdelight.api.dto.DeliveryCheckResponse;
import com.sweetdelight.api.dto.UpdateRadiusRequest;
import com.sweetdelight.api.entity.AppSetting;
import com.sweetdelight.api.exception.DeliveryOutOfRadiusException;
import com.sweetdelight.api.repository.AppSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryService {

    private final AppSettingRepository appSettingRepository;
    private final HaversineDistanceService haversineDistanceService;

    // Fallback default coordinates (Mumbai, Bandra West) if DB table is uninitialized
    private static final double DEFAULT_SHOP_LAT = 19.0596;
    private static final double DEFAULT_SHOP_LNG = 72.8295;
    private static final double DEFAULT_RADIUS_KM = 5.0;

    public DeliveryService(AppSettingRepository appSettingRepository, HaversineDistanceService haversineDistanceService) {
        this.appSettingRepository = appSettingRepository;
        this.haversineDistanceService = haversineDistanceService;
    }

    public AppSetting getOrCreateSettings() {
        return appSettingRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> {
                    AppSetting setting = new AppSetting();
                    setting.setShopLat(DEFAULT_SHOP_LAT);
                    setting.setShopLng(DEFAULT_SHOP_LNG);
                    setting.setDeliveryRadiusKm(DEFAULT_RADIUS_KM);
                    return appSettingRepository.save(setting);
                });
    }

    public AppSettingsDto getSettingsDto() {
        AppSetting setting = getOrCreateSettings();
        return new AppSettingsDto(setting.getId(), setting.getShopLat(), setting.getShopLng(), setting.getDeliveryRadiusKm(), setting.getUpdatedAt());
    }

    @Transactional
    public AppSettingsDto updateRadius(UpdateRadiusRequest request) {
        AppSetting setting = getOrCreateSettings();
        setting.setDeliveryRadiusKm(request.getDeliveryRadiusKm());
        if (request.getShopLat() != null) {
            setting.setShopLat(request.getShopLat());
        }
        if (request.getShopLng() != null) {
            setting.setShopLng(request.getShopLng());
        }
        AppSetting saved = appSettingRepository.save(setting);
        return new AppSettingsDto(saved.getId(), saved.getShopLat(), saved.getShopLng(), saved.getDeliveryRadiusKm(), saved.getUpdatedAt());
    }

    public DeliveryCheckResponse checkDelivery(DeliveryCheckRequest request) {
        AppSetting setting = getOrCreateSettings();
        double distanceKm = haversineDistanceService.calculateDistanceKm(
                setting.getShopLat(), setting.getShopLng(),
                request.getLat(), request.getLng()
        );
        double radiusKm = setting.getDeliveryRadiusKm();
        boolean deliverable = distanceKm <= radiusKm;

        String message = deliverable
                ? String.format("Delivery available! Your location is %.2f km away (within our %.1f km radius).", distanceKm, radiusKm)
                : String.format("Address is outside our delivery radius of %.1f km (distance: %.2f km).", radiusKm, distanceKm);

        return new DeliveryCheckResponse(deliverable, distanceKm, radiusKm, setting.getShopLat(), setting.getShopLng(), message);
    }

    /**
     * Validates that the coordinates are deliverable. Throws 422 DeliveryOutOfRadiusException if outside radius.
     * Returns the computed distance in km.
     */
    public double validateDeliverableOrThrow(double deliveryLat, double deliveryLng) {
        AppSetting setting = getOrCreateSettings();
        double distanceKm = haversineDistanceService.calculateDistanceKm(
                setting.getShopLat(), setting.getShopLng(),
                deliveryLat, deliveryLng
        );
        double radiusKm = setting.getDeliveryRadiusKm();

        if (distanceKm > radiusKm) {
            throw new DeliveryOutOfRadiusException(distanceKm, radiusKm);
        }
        return distanceKm;
    }
}
