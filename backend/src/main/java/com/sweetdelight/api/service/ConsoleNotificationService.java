package com.sweetdelight.api.service;

import com.sweetdelight.api.config.AppProperties;
import com.sweetdelight.api.entity.Order;
import com.sweetdelight.api.entity.OrderStatus;
import com.sweetdelight.api.entity.Payment;
import com.sweetdelight.api.entity.RoomBooking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "app.notify.mode", havingValue = "console", matchIfMissing = true)
public class ConsoleNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleNotificationService.class);
    private final AppProperties appProperties;

    public ConsoleNotificationService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public void notifyPaymentConfirmed(Order order, Payment payment) {
        try {
            String trackingLink = "/api/orders/" + order.getId() + "/tracking";

            // Customer message
            String customerMsg = String.format(
                    "[WHATSAPP CONSOLE NOTIFICATION -> CUSTOMER %s]\n" +
                    "🎉 SweetDelight: Your payment of ₹%s is confirmed!\n" +
                    "Order ID: %s | Payment ID: %s\n" +
                    "Delivery Slot: %s\n" +
                    "Track your fresh treats here: %s",
                    order.getWhatsappNumber(),
                    order.getTotalInr(),
                    order.getOrderNumber(),
                    payment != null ? payment.getRazorpayPaymentId() : "N/A",
                    order.getDeliverySlot(),
                    trackingLink
            );
            log.info("{}", customerMsg);

            // Admin message
            String itemsSummary = order.getItems().stream()
                    .map(item -> item.getProductName() + " (" + item.getVariantTitle() + ") x" + item.getQuantity())
                    .collect(Collectors.joining(", "));

            String adminMsg = String.format(
                    "[WHATSAPP CONSOLE NOTIFICATION -> ADMIN %s]\n" +
                    "🔔 NEW PAID ORDER ALERT!\n" +
                    "Order: %s\n" +
                    "Customer: %s (%s)\n" +
                    "Items: %s\n" +
                    "Amount: ₹%s\n" +
                    "Address: %s\n" +
                    "Slot: %s",
                    appProperties.getNotify().getAdminNumber(),
                    order.getOrderNumber(),
                    order.getCustomerName(),
                    order.getWhatsappNumber(),
                    itemsSummary,
                    order.getTotalInr(),
                    order.getDeliveryAddress(),
                    order.getDeliverySlot()
            );
            log.info("{}", adminMsg);
        } catch (Exception e) {
            log.error("Failed to log payment confirmed console notification: {}", e.getMessage());
        }
    }

    @Override
    public void notifyCodOrderPlaced(Order order) {
        try {
            String trackingLink = "/api/orders/" + order.getId() + "/tracking";

            // Customer message
            String customerMsg = String.format(
                    "[WHATSAPP CONSOLE NOTIFICATION -> CUSTOMER %s]\n" +
                    "🍰 SweetDelight: Order %s placed with Cash on Delivery (COD)!\n" +
                    "Total Amount: ₹%s (payable upon delivery)\n" +
                    "Slot: %s\n" +
                    "Track status: %s",
                    order.getWhatsappNumber(),
                    order.getOrderNumber(),
                    order.getTotalInr(),
                    order.getDeliverySlot(),
                    trackingLink
            );
            log.info("{}", customerMsg);

            // Admin message
            String itemsSummary = order.getItems().stream()
                    .map(item -> item.getProductName() + " (" + item.getVariantTitle() + ") x" + item.getQuantity())
                    .collect(Collectors.joining(", "));

            String adminMsg = String.format(
                    "[WHATSAPP CONSOLE NOTIFICATION -> ADMIN %s]\n" +
                    "📦 NEW COD ORDER PLACED!\n" +
                    "Order: %s | Customer: %s (%s)\n" +
                    "Items: %s | Total: ₹%s\n" +
                    "Address: %s | Slot: %s",
                    appProperties.getNotify().getAdminNumber(),
                    order.getOrderNumber(),
                    order.getCustomerName(),
                    order.getWhatsappNumber(),
                    itemsSummary,
                    order.getTotalInr(),
                    order.getDeliveryAddress(),
                    order.getDeliverySlot()
            );
            log.info("{}", adminMsg);
        } catch (Exception e) {
            log.error("Failed to log COD order console notification: {}", e.getMessage());
        }
    }

    @Override
    public void notifyOrderStatusChanged(Order order, OrderStatus newStatus, String notes) {
        try {
            String message = String.format(
                    "[WHATSAPP CONSOLE NOTIFICATION -> CUSTOMER %s]\n" +
                    "🎂 SweetDelight: Your order %s is now %s! %s",
                    order.getWhatsappNumber(),
                    order.getOrderNumber(),
                    newStatus,
                    notes != null && !notes.isBlank() ? "(" + notes + ")" : ""
            );
            log.info("{}", message);
        } catch (Exception e) {
            log.error("Failed to log status change notification: {}", e.getMessage());
        }
    }

    @Override
    public void notifyOutForDelivery(Order order, String eta) {
        try {
            String message = String.format(
                    "[WHATSAPP CONSOLE NOTIFICATION -> CUSTOMER %s]\n" +
                    "🛵 SweetDelight: Your treats are OUT FOR DELIVERY!\n" +
                    "Order: %s | ETA: %s\n" +
                    "Rider: %s (%s)\n" +
                    "Track live location: /api/orders/%s/tracking",
                    order.getWhatsappNumber(),
                    order.getOrderNumber(),
                    eta != null ? eta : "within 30-45 mins",
                    order.getRiderName() != null ? order.getRiderName() : "Assigned Rider",
                    order.getRiderPhone() != null ? order.getRiderPhone() : "N/A",
                    order.getId()
            );
            log.info("{}", message);
        } catch (Exception e) {
            log.error("Failed to log out for delivery notification: {}", e.getMessage());
        }
    }

    @Override
    public void notifyPaymentFailed(Order order, String retryUrl) {
        try {
            String message = String.format(
                    "[WHATSAPP CONSOLE NOTIFICATION -> CUSTOMER %s]\n" +
                    "⚠️ SweetDelight: Payment for order %s failed or was interrupted.\n" +
                    "You can retry your payment here: %s",
                    order.getWhatsappNumber(),
                    order.getOrderNumber(),
                    retryUrl != null ? retryUrl : "/api/orders/" + order.getId() + "/payment"
            );
            log.info("{}", message);
        } catch (Exception e) {
            log.error("Failed to log payment failed notification: {}", e.getMessage());
        }
    }

    @Override
    public void notifyRefundProcessed(Order order, Payment payment) {
        try {
            String message = String.format(
                    "[WHATSAPP CONSOLE NOTIFICATION -> CUSTOMER %s]\n" +
                    "💸 SweetDelight: Full refund of ₹%s has been initiated for order %s.\n" +
                    "Refund will reflect in your original payment method in 5-7 business days.",
                    order.getWhatsappNumber(),
                    order.getTotalInr(),
                    order.getOrderNumber()
            );
            log.info("{}", message);
        } catch (Exception e) {
            log.error("Failed to log refund notification: {}", e.getMessage());
        }
    }

    @Override
    public void notifyBookingDecision(RoomBooking booking) {
        try {
            String message = String.format(
                    "[WHATSAPP CONSOLE NOTIFICATION -> CUSTOMER %s]\n" +
                    "🎈 SweetDelight Party Room Booking Update:\n" +
                    "Room: %s | Date: %s (%s - %s)\n" +
                    "Status: %s\n" +
                    "%s",
                    booking.getContactPhone(),
                    booking.getRoom().getName(),
                    booking.getBookingDate(),
                    booking.getStartTime(),
                    booking.getEndTime(),
                    booking.getStatus(),
                    booking.getNotes() != null ? "Note: " + booking.getNotes() : ""
            );
            log.info("{}", message);
        } catch (Exception e) {
            log.error("Failed to log booking decision notification: {}", e.getMessage());
        }
    }
}
