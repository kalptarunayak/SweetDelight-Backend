package com.sweetdelight.api.service;

import com.sweetdelight.api.config.AppProperties;
import com.sweetdelight.api.entity.Order;
import com.sweetdelight.api.entity.OrderStatus;
import com.sweetdelight.api.entity.Payment;
import com.sweetdelight.api.entity.RoomBooking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "app.notify.mode", havingValue = "whatsapp")
public class WhatsAppNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppNotificationService.class);
    private final AppProperties appProperties;
    private final WebClient webClient;

    public WhatsAppNotificationService(AppProperties appProperties) {
        this.appProperties = appProperties;
        this.webClient = WebClient.builder()
                .baseUrl("https://graph.facebook.com/v19.0")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private void sendWhatsAppMessage(String toE164Number, String textBody) {
        try {
            String phoneId = appProperties.getNotify().getWhatsappPhoneId();
            String token = appProperties.getNotify().getWhatsappToken();

            if (phoneId == null || phoneId.isBlank() || token == null || token.isBlank()) {
                log.warn("WhatsApp credentials not set. Falling back to log for [{}]: {}", toE164Number, textBody);
                return;
            }

            // Standard Meta Cloud API message payload
            Map<String, Object> payload = Map.of(
                    "messaging_product", "whatsapp",
                    "recipient_type", "individual",
                    "to", toE164Number.replace("+", ""),
                    "type", "text",
                    "text", Map.of("preview_url", true, "body", textBody)
            );

            webClient.post()
                    .uri("/{phoneId}/messages", phoneId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(resp -> log.info("WhatsApp message sent successfully to {}", toE164Number))
                    .doOnError(err -> log.error("Failed to send WhatsApp message to {}: {}", toE164Number, err.getMessage()))
                    .subscribe();
        } catch (Exception e) {
            log.error("Exception in sendWhatsAppMessage: {}", e.getMessage());
        }
    }

    @Override
    public void notifyPaymentConfirmed(Order order, Payment payment) {
        try {
            String customerMsg = String.format(
                    "🎉 SweetDelight: Your payment of ₹%s is confirmed! Order ID: %s. Slot: %s. Track: /api/orders/%s/tracking",
                    order.getTotalInr(), order.getOrderNumber(), order.getDeliverySlot(), order.getId()
            );
            sendWhatsAppMessage(order.getWhatsappNumber(), customerMsg);

            String itemsSummary = order.getItems().stream()
                    .map(item -> item.getProductName() + " (" + item.getVariantTitle() + ") x" + item.getQuantity())
                    .collect(Collectors.joining(", "));
            String adminMsg = String.format(
                    "🔔 NEW PAID ORDER! ID: %s | Customer: %s (%s) | Items: %s | Amount: ₹%s | Slot: %s | Address: %s",
                    order.getOrderNumber(), order.getCustomerName(), order.getWhatsappNumber(), itemsSummary,
                    order.getTotalInr(), order.getDeliverySlot(), order.getDeliveryAddress()
            );
            sendWhatsAppMessage(appProperties.getNotify().getAdminNumber(), adminMsg);
        } catch (Exception e) {
            log.error("Failed to send WhatsApp payment notification: {}", e.getMessage());
        }
    }

    @Override
    public void notifyCodOrderPlaced(Order order) {
        try {
            String customerMsg = String.format(
                    "🍰 SweetDelight: Order %s placed with Cash on Delivery! Total: ₹%s. Slot: %s. Track: /api/orders/%s/tracking",
                    order.getOrderNumber(), order.getTotalInr(), order.getDeliverySlot(), order.getId()
            );
            sendWhatsAppMessage(order.getWhatsappNumber(), customerMsg);

            String itemsSummary = order.getItems().stream()
                    .map(item -> item.getProductName() + " (" + item.getVariantTitle() + ") x" + item.getQuantity())
                    .collect(Collectors.joining(", "));
            String adminMsg = String.format(
                    "📦 NEW COD ORDER! ID: %s | Customer: %s (%s) | Items: %s | Amount: ₹%s | Slot: %s",
                    order.getOrderNumber(), order.getCustomerName(), order.getWhatsappNumber(), itemsSummary,
                    order.getTotalInr(), order.getDeliverySlot()
            );
            sendWhatsAppMessage(appProperties.getNotify().getAdminNumber(), adminMsg);
        } catch (Exception e) {
            log.error("Failed to send WhatsApp COD notification: {}", e.getMessage());
        }
    }

    @Override
    public void notifyOrderStatusChanged(Order order, OrderStatus newStatus, String notes) {
        try {
            String msg = String.format(
                    "🎂 SweetDelight: Order %s status updated to %s. %s",
                    order.getOrderNumber(), newStatus, notes != null && !notes.isBlank() ? "(" + notes + ")" : ""
            );
            sendWhatsAppMessage(order.getWhatsappNumber(), msg);
        } catch (Exception e) {
            log.error("Failed to send WhatsApp status change notification: {}", e.getMessage());
        }
    }

    @Override
    public void notifyOutForDelivery(Order order, String eta) {
        try {
            String msg = String.format(
                    "🛵 SweetDelight: Your order %s is OUT FOR DELIVERY! ETA: %s. Rider: %s (%s). Track: /api/orders/%s/tracking",
                    order.getOrderNumber(), eta != null ? eta : "30-45 mins",
                    order.getRiderName() != null ? order.getRiderName() : "Rider",
                    order.getRiderPhone() != null ? order.getRiderPhone() : "N/A",
                    order.getId()
            );
            sendWhatsAppMessage(order.getWhatsappNumber(), msg);
        } catch (Exception e) {
            log.error("Failed to send WhatsApp out for delivery notification: {}", e.getMessage());
        }
    }

    @Override
    public void notifyPaymentFailed(Order order, String retryUrl) {
        try {
            String msg = String.format(
                    "⚠️ SweetDelight: Payment for order %s failed. Retry here: %s",
                    order.getOrderNumber(), retryUrl
            );
            sendWhatsAppMessage(order.getWhatsappNumber(), msg);
        } catch (Exception e) {
            log.error("Failed to send WhatsApp payment failure notification: {}", e.getMessage());
        }
    }

    @Override
    public void notifyRefundProcessed(Order order, Payment payment) {
        try {
            String msg = String.format(
                    "💸 SweetDelight: Full refund of ₹%s has been processed for order %s.",
                    order.getTotalInr(), order.getOrderNumber()
            );
            sendWhatsAppMessage(order.getWhatsappNumber(), msg);
        } catch (Exception e) {
            log.error("Failed to send WhatsApp refund notification: {}", e.getMessage());
        }
    }

    @Override
    public void notifyBookingDecision(RoomBooking booking) {
        try {
            String msg = String.format(
                    "🎈 SweetDelight Party Room Booking: Room %s on %s (%s - %s) has been %s. %s",
                    booking.getRoom().getName(), booking.getBookingDate(),
                    booking.getStartTime(), booking.getEndTime(),
                    booking.getStatus(), booking.getNotes() != null ? "Note: " + booking.getNotes() : ""
            );
            sendWhatsAppMessage(booking.getContactPhone(), msg);
        } catch (Exception e) {
            log.error("Failed to send WhatsApp booking decision notification: {}", e.getMessage());
        }
    }
}
