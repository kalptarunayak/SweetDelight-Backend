package com.sweetdelight.api.controller;

import com.sweetdelight.api.dto.CreateProductRequest;
import com.sweetdelight.api.dto.ProductDto;
import com.sweetdelight.api.dto.UpdateProductRequest;
import com.sweetdelight.api.dto.UpdateStockRequest;
import com.sweetdelight.api.service.CatalogService;
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

import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
@Tag(name = "Admin Products", description = "Admin CRUD operations for catalog products and variants")
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final CatalogService catalogService;

    public AdminProductController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Operation(summary = "Create product with variants (Admin)",
               description = "Creates a new catalog product (e.g. artisan cake or chocolate) with its variants (weights/sizes, prices). Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error in product or variants payload"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductDto created = catalogService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Update product and variants (Admin)",
               description = "Updates an existing product's name, description, category, and variants. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Product or Category not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(catalogService.updateProduct(id, request));
    }

    @Operation(summary = "Delete product (Admin)", description = "Deletes a product and all its variants. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id) {
        catalogService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message", "Product deleted successfully", "id", id.toString()));
    }

    @Operation(summary = "Update product stock status (Admin)", description = "Toggles product availability. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock status updated"),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductDto> updateStock(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateStockRequest request) {
        return ResponseEntity.ok(catalogService.updateStock(id, request.getInStock()));
    }
}
