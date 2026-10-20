package com.sweetdelight.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class UpdateProductRequest {

    @Schema(description = "Category ID", example = "1")
    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @Schema(description = "Product name", example = "Belgian Chocolate Truffle Cake")
    @NotBlank(message = "Product name is required")
    private String name;

    @Schema(description = "Detailed product description", example = "Updated product description.")
    private String description;

    @Schema(description = "Image URL", example = "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=800")
    private String imageUrl;

    @Schema(description = "Whether the product is in stock", example = "true")
    private boolean inStock;

    @Schema(description = "Optional updated list of variants")
    @Valid
    private List<CreateVariantRequest> variants;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public List<CreateVariantRequest> getVariants() {
        return variants;
    }

    public void setVariants(List<CreateVariantRequest> variants) {
        this.variants = variants;
    }
}
