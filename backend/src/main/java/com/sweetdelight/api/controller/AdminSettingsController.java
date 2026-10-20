package com.sweetdelight.api.controller;

import com.sweetdelight.api.dto.AppSettingsDto;
import com.sweetdelight.api.dto.UpdateRadiusRequest;
import com.sweetdelight.api.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/settings")
@Tag(name = "Admin Settings", description = "Store manager settings: delivery radius and shop coordinates")
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSettingsController {

    private final DeliveryService deliveryService;

    public AdminSettingsController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Operation(summary = "Get shop settings (Admin)", description = "Retrieves store coordinates and current delivery radius in kilometers. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Settings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @GetMapping
    public ResponseEntity<AppSettingsDto> getSettings() {
        return ResponseEntity.ok(deliveryService.getSettingsDto());
    }

    @Operation(summary = "Update delivery radius and coordinates (Admin)",
               description = "Updates the maximum delivery radius in km and optionally shop lat/lng. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Radius and settings updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error in radius or coordinates"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @PutMapping("/radius")
    public ResponseEntity<AppSettingsDto> updateRadius(@Valid @RequestBody UpdateRadiusRequest request) {
        return ResponseEntity.ok(deliveryService.updateRadius(request));
    }
}
