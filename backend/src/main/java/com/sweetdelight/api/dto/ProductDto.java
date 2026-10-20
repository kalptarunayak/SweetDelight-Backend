package com.sweetdelight.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductDto {

    @Schema(description = "Product ID", example = "1")
    private Long id;

    @Schema(description = "Product Name", example = "Belgian Chocolate Truffle Cake")
    private String name;

    @Schema(description = "Product Description", example = "Rich 54% dark chocolate ganache layered with moist chocolate sponge.")
    private String description;

    @Schema(description = "Product image URL", example = "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=800")
    private String imageUrl;

    @Schema(description = "Category ID", example = "1")
    private Long categoryId;

    @Schema(description = "Category Name", example = "Cakes")
    private String categoryName;

    @Schema(description = "Whether the product is in stock", example = "true")
    private boolean inStock;

    @Schema(description = "Product Variants (sizes/weights and prices)")
    private List<ProductVariantDto> variants = new ArrayList<>();

    @Schema(description = "Created timestamp")
    private OffsetDateTime createdAt;

    public ProductDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public List<ProductVariantDto> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariantDto> variants) {
        this.variants = variants;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
