package com.sweetdelight.api.controller;

import com.sweetdelight.api.dto.DeliveryCheckRequest;
import com.sweetdelight.api.dto.DeliveryCheckResponse;
import com.sweetdelight.api.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/delivery")
@Tag(name = "Delivery", description = "Public delivery check using Haversine formula against shop radius")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Operation(summary = "Check delivery availability for coordinates",
               description = "Accepts customer delivery latitude and longitude, calculates Haversine distance from the shop location, and checks against the store delivery radius.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delivery distance and eligibility computed"),
            @ApiResponse(responseCode = "400", description = "Invalid latitude or longitude format")
    })
    @PostMapping("/check")
    public ResponseEntity<DeliveryCheckResponse> checkDelivery(@Valid @RequestBody DeliveryCheckRequest request) {
        return ResponseEntity.ok(deliveryService.checkDelivery(request));
    }
}
