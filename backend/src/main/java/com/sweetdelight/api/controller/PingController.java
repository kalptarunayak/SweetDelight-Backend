package com.sweetdelight.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ping")
@Tag(name = "Catalog", description = "Store health and public endpoints")
public class PingController {

    @Operation(summary = "Health check and wake-up ping for frontend",
               description = "Returns status ok so the frontend can wake up the Render free-tier service.")
    @ApiResponse(responseCode = "200", description = "Server is active and healthy")
    @GetMapping
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
