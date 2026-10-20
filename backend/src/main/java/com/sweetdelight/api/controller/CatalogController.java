package com.sweetdelight.api.controller;

import com.sweetdelight.api.dto.CategoryDto;
import com.sweetdelight.api.dto.ProductDto;
import com.sweetdelight.api.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Catalog", description = "Public catalog endpoints to browse categories, products, and variants")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Operation(summary = "List all product categories", description = "Returns active categories like Cakes and Chocolates.")
    @ApiResponse(responseCode = "200", description = "Categories fetched successfully")
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getCategories() {
        return ResponseEntity.ok(catalogService.getCategories());
    }

    @Operation(summary = "List products with variants",
               description = "Returns products with their respective variants (sizes/weights, prices). Filterable by category and stock availability.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameters")
    })
    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getProducts(
            @Parameter(description = "Optional category ID filter (1=Cakes, 2=Chocolates)", example = "1")
            @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Filter only products currently in stock", example = "false")
            @RequestParam(defaultValue = "false") boolean inStockOnly) {
        return ResponseEntity.ok(catalogService.getProducts(categoryId, inStockOnly));
    }

    @Operation(summary = "Get product by ID", description = "Returns full product details and all variants for a given ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product details retrieved"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDto> getProductById(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getProductById(id));
    }
}
