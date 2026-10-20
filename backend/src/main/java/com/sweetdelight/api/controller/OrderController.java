package com.sweetdelight.api.controller;

import com.sweetdelight.api.dto.CreateOrderRequest;
import com.sweetdelight.api.dto.OrderDto;
import com.sweetdelight.api.security.UserPrincipal;
import com.sweetdelight.api.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Customer order placement and retrieval")
@SecurityRequirement(name = "BearerAuth")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Place a new cake & chocolate order",
               description = "Creates a new customer order. The server strictly computes all item prices, variant prices, promotional discounts and totals from the database and verifies that the delivery address is within the store delivery radius via Haversine calculation. Rejects with 422 if outside delivery radius.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload, out of stock item, or COD disabled"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Bearer token required"),
            @ApiResponse(responseCode = "422", description = "Delivery location is outside the store delivery radius")
    })
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateOrderRequest request) {
        OrderDto created = orderService.createOrder(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "List customer orders", description = "Retrieves all past and active orders for the currently authenticated customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<OrderDto>> getMyOrders(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(orderService.getCustomerOrders(principal.getId()));
    }

    @Operation(summary = "Get order details by ID", description = "Retrieves order details. Customers can view their own orders; admins can view any order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not order owner"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "Order ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id, principal.getId(), principal.isAdmin()));
    }
}
