package com.sweetdelight.api.controller;

import com.sweetdelight.api.dto.OrderTrackingDto;
import com.sweetdelight.api.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Tracking", description = "Customer real-time order status timeline and rider location tracking")
public class TrackingController {

    private final OrderService orderService;

    public TrackingController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Track order status and rider location",
               description = "Returns the order status timeline, estimated delivery time, and real-time rider latitude/longitude coordinates if assigned.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tracking information retrieved"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}/tracking")
    public ResponseEntity<OrderTrackingDto> getTracking(
            @Parameter(description = "Order ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderTracking(id));
    }
}
