package com.sweetdelight.api.controller;

import com.sweetdelight.api.dto.BannerDto;
import com.sweetdelight.api.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
@Tag(name = "Banners", description = "Public active promotional banners and discount coupons")
public class BannerController {

    private final BannerService bannerService;

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @Operation(summary = "Get active published banners",
               description = "Returns promotional banners that are published and within their start_at and end_at time window. Contains coupon codes and discount percentages.")
    @ApiResponse(responseCode = "200", description = "Active banners retrieved successfully")
    @GetMapping
    public ResponseEntity<List<BannerDto>> getActiveBanners() {
        return ResponseEntity.ok(bannerService.getActiveBanners());
    }
}
