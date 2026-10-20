package com.sweetdelight.api.controller;

import com.sweetdelight.api.dto.OrderDto;
import com.sweetdelight.api.dto.PaymentDto;
import com.sweetdelight.api.dto.UpdateOrderStatusRequest;
import com.sweetdelight.api.dto.UpdateRiderLocationRequest;
import com.sweetdelight.api.entity.OrderStatus;
import com.sweetdelight.api.service.OrderService;
import com.sweetdelight.api.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@Tag(name = "Admin Orders", description = "Store manager order lifecycle management, status updates, rider coordinates, and refunds")
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    public AdminOrderController(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @Operation(summary = "List all orders (Admin)", description = "Retrieves all store orders with optional status filtering. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders(
            @Parameter(description = "Optional status filter (e.g. PLACED, CONFIRMED, BAKING, OUT_FOR_DELIVERY)", example = "PLACED")
            @RequestParam(required = false) OrderStatus status) {
        return ResponseEntity.ok(orderService.getAdminOrders(status));
    }

    @Operation(summary = "Update order status & rider info (Admin)",
               description = "Transitions order status (CONFIRMED, BAKING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED), sets ETA and rider details, records status timeline event, and notifies customer via WhatsApp/Console. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order status updated and customer notified"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDto> updateStatus(
            @Parameter(description = "Order ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request));
    }

    @Operation(summary = "Update rider real-time location (Admin)",
               description = "Updates the rider's latest latitude and longitude coordinates during delivery. The tracking endpoint returns these live coordinates. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rider location updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{id}/location")
    public ResponseEntity<OrderDto> updateRiderLocation(
            @Parameter(description = "Order ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateRiderLocationRequest request) {
        return ResponseEntity.ok(orderService.updateRiderLocation(id, request));
    }

    @Operation(summary = "Issue full Razorpay refund & cancel order (Admin)",
               description = "Invokes Razorpay refund API for full amount, sets payment status to REFUNDED, transitions order to CANCELLED, and triggers customer notification. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Full refund issued and order cancelled"),
            @ApiResponse(responseCode = "400", description = "Payment not in refundable state or already refunded"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Order or Payment not found")
    })
    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentDto> refundOrder(
            @Parameter(description = "Order ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.processRefund(id));
    }
}
