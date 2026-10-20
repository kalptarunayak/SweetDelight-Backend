package com.sweetdelight.api.service;

import com.sweetdelight.api.config.AppProperties;
import com.sweetdelight.api.dto.*;
import com.sweetdelight.api.entity.*;
import com.sweetdelight.api.exception.BadRequestException;
import com.sweetdelight.api.exception.ForbiddenException;
import com.sweetdelight.api.exception.ResourceNotFoundException;
import com.sweetdelight.api.repository.OrderRepository;
import com.sweetdelight.api.repository.OrderStatusEventRepository;
import com.sweetdelight.api.repository.ProductVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OrderStatusEventRepository statusEventRepository;
    private final DeliveryService deliveryService;
    private final BannerService bannerService;
    private final NotificationService notificationService;
    private final AppProperties appProperties;

    public OrderService(OrderRepository orderRepository,
                        ProductVariantRepository productVariantRepository,
                        OrderStatusEventRepository statusEventRepository,
                        DeliveryService deliveryService,
                        BannerService bannerService,
                        NotificationService notificationService,
                        AppProperties appProperties) {
        this.orderRepository = orderRepository;
        this.productVariantRepository = productVariantRepository;
        this.statusEventRepository = statusEventRepository;
        this.deliveryService = deliveryService;
        this.bannerService = bannerService;
        this.notificationService = notificationService;
        this.appProperties = appProperties;
    }

    @Transactional
    public OrderDto createOrder(UUID customerId, CreateOrderRequest request) {
        // 1. Haversine distance server-side check (throws 422 DeliveryOutOfRadiusException if > radius)
        double distanceKm = deliveryService.validateDeliverableOrThrow(request.getDeliveryLat(), request.getDeliveryLng());

        // 2. Check COD enabled flag if COD requested
        if (request.getPaymentMethod() == PaymentMethod.COD && !appProperties.getShop().isCodEnabled()) {
            throw new BadRequestException("Cash on Delivery (COD) is currently disabled. Please choose ONLINE payment.");
        }

        // 3. Recompute all prices strictly from database
        BigDecimal subtotal = BigDecimal.ZERO;
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setCustomerName(request.getCustomerName().trim());
        order.setWhatsappNumber(request.getWhatsappNumber().trim());
        order.setDeliveryAddress(request.getDeliveryAddress().trim());
        order.setDeliveryLat(request.getDeliveryLat());
        order.setDeliveryLng(request.getDeliveryLng());
        order.setDeliveryDistanceKm(distanceKm);
        order.setDeliverySlot(request.getDeliverySlot().trim());
        order.setNote(request.getNote());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setOrderNumber(generateOrderNumber());

        for (CreateOrderItemRequest itemReq : request.getItems()) {
            ProductVariant variant = productVariantRepository.findById(itemReq.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with id: " + itemReq.getVariantId()));

            Product product = variant.getProduct();
            if (!product.isInStock() || !variant.isInStock()) {
                throw new BadRequestException(String.format("Item '%s (%s)' is currently out of stock",
                        product.getName(), variant.getTitle()));
            }

            BigDecimal itemTotal = variant.getPriceInr().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            subtotal = subtotal.add(itemTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setVariantId(variant.getId());
            orderItem.setProductName(product.getName());
            orderItem.setVariantTitle(variant.getTitle());
            orderItem.setUnitPriceInr(variant.getPriceInr());
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setTotalPriceInr(itemTotal);

            order.addItem(orderItem);
        }

        order.setSubtotalInr(subtotal);

        // 4. Server-computed coupon discount
        BigDecimal discount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            discount = bannerService.calculateDiscount(request.getCouponCode(), subtotal);
            order.setCouponCode(request.getCouponCode().trim().toUpperCase());
        }
        order.setDiscountInr(discount);

        BigDecimal total = subtotal.subtract(discount);
        order.setTotalInr(total.max(BigDecimal.ZERO));

        // 5. Initial status
        if (request.getPaymentMethod() == PaymentMethod.COD) {
            order.setStatus(OrderStatus.PLACED);
        } else {
            order.setStatus(OrderStatus.PENDING_PAYMENT);
        }

        Order saved = orderRepository.save(order);

        // 6. Record initial status event
        String initialEventNote = request.getPaymentMethod() == PaymentMethod.COD
                ? "Order placed with Cash on Delivery"
                : "Order created, waiting for online payment completion";
        OrderStatusEvent initialEvent = new OrderStatusEvent(saved, saved.getStatus(), initialEventNote);
        statusEventRepository.save(initialEvent);
        saved.getStatusEvents().add(initialEvent);

        // 7. If COD order, notify customer and admin immediately
        if (request.getPaymentMethod() == PaymentMethod.COD) {
            notificationService.notifyCodOrderPlaced(saved);
        }

        return mapToDto(saved);
    }

    public OrderDto getOrderById(Long id, UUID currentUserId, boolean isAdmin) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (!isAdmin && !order.getCustomerId().equals(currentUserId)) {
            throw new ForbiddenException("You are not authorized to view this order");
        }

        return mapToDto(order);
    }

    public List<OrderDto> getCustomerOrders(UUID customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<OrderDto> getAdminOrders(OrderStatus status) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();

        order.setStatus(newStatus);
        if (request.getEstimatedDeliveryTime() != null) {
            order.setEstimatedDeliveryTime(request.getEstimatedDeliveryTime());
        }
        if (request.getRiderName() != null) {
            order.setRiderName(request.getRiderName());
        }
        if (request.getRiderPhone() != null) {
            order.setRiderPhone(request.getRiderPhone());
        }

        Order saved = orderRepository.save(order);

        OrderStatusEvent event = new OrderStatusEvent(saved, newStatus, request.getNotes());
        statusEventRepository.save(event);
        saved.getStatusEvents().add(event);

        // Send notifications based on status
        if (newStatus == OrderStatus.OUT_FOR_DELIVERY) {
            String etaStr = request.getEstimatedDeliveryTime() != null
                    ? request.getEstimatedDeliveryTime().format(DateTimeFormatter.ofPattern("hh:mm a"))
                    : "within 30-45 mins";
            notificationService.notifyOutForDelivery(saved, etaStr);
        } else {
            notificationService.notifyOrderStatusChanged(saved, newStatus, request.getNotes());
        }

        return mapToDto(saved);
    }

    @Transactional
    public OrderDto updateRiderLocation(Long orderId, UpdateRiderLocationRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setRiderLat(request.getLat());
        order.setRiderLng(request.getLng());
        order.setRiderLocationUpdatedAt(OffsetDateTime.now());

        Order saved = orderRepository.save(order);
        return mapToDto(saved);
    }

    public OrderTrackingDto getOrderTracking(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        OrderTrackingDto dto = new OrderTrackingDto();
        dto.setOrderId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus());
        dto.setEstimatedDeliveryTime(order.getEstimatedDeliveryTime());
        dto.setRiderName(order.getRiderName());
        dto.setRiderPhone(order.getRiderPhone());
        dto.setRiderLat(order.getRiderLat());
        dto.setRiderLng(order.getRiderLng());
        dto.setRiderLocationUpdatedAt(order.getRiderLocationUpdatedAt());

        List<OrderStatusEvent> events = statusEventRepository.findByOrderIdOrderByCreatedAtAsc(order.getId());
        dto.setTimeline(events.stream()
                .map(e -> new OrderStatusEventDto(e.getId(), e.getStatus(), e.getNotes(), e.getCreatedAt()))
                .collect(Collectors.toList()));

        return dto;
    }

    private String generateOrderNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int rand = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "ORD-" + date + "-" + rand;
    }

    public OrderDto mapToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setCustomerId(order.getCustomerId());
        dto.setCustomerName(order.getCustomerName());
        dto.setWhatsappNumber(order.getWhatsappNumber());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setDeliveryLat(order.getDeliveryLat());
        dto.setDeliveryLng(order.getDeliveryLng());
        dto.setDeliveryDistanceKm(order.getDeliveryDistanceKm());
        dto.setDeliverySlot(order.getDeliverySlot());
        dto.setNote(order.getNote());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setSubtotalInr(order.getSubtotalInr());
        dto.setDiscountInr(order.getDiscountInr());
        dto.setCouponCode(order.getCouponCode());
        dto.setTotalInr(order.getTotalInr());
        dto.setStatus(order.getStatus());
        dto.setEstimatedDeliveryTime(order.getEstimatedDeliveryTime());
        dto.setRiderName(order.getRiderName());
        dto.setRiderPhone(order.getRiderPhone());
        dto.setRiderLat(order.getRiderLat());
        dto.setRiderLng(order.getRiderLng());
        dto.setRiderLocationUpdatedAt(order.getRiderLocationUpdatedAt());
        dto.setCreatedAt(order.getCreatedAt());

        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream().map(i -> {
                OrderItemDto itemDto = new OrderItemDto();
                itemDto.setId(i.getId());
                itemDto.setProductId(i.getProductId());
                itemDto.setVariantId(i.getVariantId());
                itemDto.setProductName(i.getProductName());
                itemDto.setVariantTitle(i.getVariantTitle());
                itemDto.setUnitPriceInr(i.getUnitPriceInr());
                itemDto.setQuantity(i.getQuantity());
                itemDto.setTotalPriceInr(i.getTotalPriceInr());
                return itemDto;
            }).collect(Collectors.toList()));
        }

        if (order.getStatusEvents() != null) {
            dto.setStatusEvents(order.getStatusEvents().stream()
                    .map(e -> new OrderStatusEventDto(e.getId(), e.getStatus(), e.getNotes(), e.getCreatedAt()))
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
