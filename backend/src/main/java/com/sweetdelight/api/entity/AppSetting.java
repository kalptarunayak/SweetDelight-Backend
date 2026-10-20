package com.sweetdelight.api.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "app_settings")
public class AppSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_lat", nullable = false)
    private Double shopLat;

    @Column(name = "shop_lng", nullable = false)
    private Double shopLng;

    @Column(name = "delivery_radius_km", nullable = false)
    private Double deliveryRadiusKm = 5.0;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void prePersistOrUpdate() {
        this.updatedAt = OffsetDateTime.now();
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
