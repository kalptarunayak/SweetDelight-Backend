package com.sweetdelight.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class CategoryDto {

    @Schema(description = "Category ID", example = "1")
    private Long id;

    @Schema(description = "Category Name", example = "Cakes")
    private String name;

    @Schema(description = "Category URL slug", example = "cakes")
    private String slug;

    @Schema(description = "Category description", example = "Freshly baked artisanal cakes")
    private String description;

    public CategoryDto() {}

    public CategoryDto(Long id, String name, String slug, String description) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
    }

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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
