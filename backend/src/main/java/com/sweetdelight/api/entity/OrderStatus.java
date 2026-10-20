package com.sweetdelight.api.entity;

public enum OrderStatus {
    PENDING_PAYMENT,
    PLACED,
    CONFIRMED,
    BAKING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED,
    PAYMENT_FAILED
}
