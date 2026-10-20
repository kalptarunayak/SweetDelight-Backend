package com.sweetdelight.api.controller;

import com.sweetdelight.api.dto.BannerDto;
import com.sweetdelight.api.dto.CreateBannerRequest;
import com.sweetdelight.api.dto.UpdateBannerRequest;
import com.sweetdelight.api.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/banners")
@Tag(name = "Admin Banners", description = "Admin CRUD operations for promotional banners and coupon codes")
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBannerController {

    private final BannerService bannerService;

    public AdminBannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @Operation(summary = "List all banners (Admin)", description = "Returns all banners including inactive and expired campaigns. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Banners retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @GetMapping
    public ResponseEntity<List<BannerDto>> getAllBanners() {
        return ResponseEntity.ok(bannerService.getAllBanners());
    }

    @Operation(summary = "Create promotional banner (Admin)",
               description = "Creates a new promotional banner with optional discount percentage and coupon code. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Banner created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or duplicate coupon code"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    public ResponseEntity<BannerDto> createBanner(@Valid @RequestBody CreateBannerRequest request) {
        BannerDto created = bannerService.createBanner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Update banner (Admin)", description = "Updates banner details, coupon code, dates, and publication status. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Banner updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Banner not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BannerDto> updateBanner(
            @Parameter(description = "Banner ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateBannerRequest request) {
        return ResponseEntity.ok(bannerService.updateBanner(id, request));
    }

    @Operation(summary = "Delete banner (Admin)", description = "Deletes a promotional banner. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Banner deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Banner not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBanner(
            @Parameter(description = "Banner ID", example = "1") @PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.ok(Map.of("message", "Banner deleted successfully", "id", id.toString()));
    }
}
