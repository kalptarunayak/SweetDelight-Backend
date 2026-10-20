package com.sweetdelight.api.service;

import com.sweetdelight.api.entity.Order;
import com.sweetdelight.api.entity.OrderStatus;
import com.sweetdelight.api.entity.Payment;
import com.sweetdelight.api.entity.RoomBooking;

public interface NotificationService {

    /**
     * Sent to customer and admin when an online payment is confirmed.
     */
    void notifyPaymentConfirmed(Order order, Payment payment);

    /**
     * Sent to customer and admin when a COD order is placed.
     */
    void notifyCodOrderPlaced(Order order);

    /**
     * Sent to customer on order status changes (e.g. CONFIRMED, BAKING, DELIVERED, CANCELLED).
     */
    void notifyOrderStatusChanged(Order order, OrderStatus newStatus, String notes);

    /**
     * Sent to customer when order is out for delivery with ETA.
     */
    void notifyOutForDelivery(Order order, String eta);

    /**
     * Sent to customer on payment failure with retry link.
     */
    void notifyPaymentFailed(Order order, String retryUrl);

    /**
     * Sent to customer when refund has been processed.
     */
    void notifyRefundProcessed(Order order, Payment payment);

    /**
     * Sent to customer when a party room booking is approved or rejected.
     */
    void notifyBookingDecision(RoomBooking booking);
}
