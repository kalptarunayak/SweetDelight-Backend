package com.sweetdelight.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public class CreateBannerRequest {

    @Schema(description = "Banner Title", example = "Grand Festive Sale")
    @NotBlank(message = "Title is required")
    private String title;

    @Schema(description = "Banner Subtitle", example = "Flat 15% OFF on handcrafted cakes & artisanal chocolates")
    private String subtitle;

    @Schema(description = "Image URL", example = "https://images.unsplash.com/photo-1535141192574-5d4897c13136?w=1200")
    private String imageUrl;

    @Schema(description = "Discount percentage (0-100)", example = "15")
    @NotNull(message = "Discount percentage is required")
    @Min(value = 0, message = "Discount cannot be negative")
    @Max(value = 100, message = "Discount cannot exceed 100%")
    private Integer discountPercent;

    @Schema(description = "Coupon Code (applied at checkout)", example = "FESTIVE15")
    private String couponCode;

    @Schema(description = "Hex Accent Color", example = "#E11D48")
    private String accentColor;

    @Schema(description = "Campaign Start Timestamp")
    @NotNull(message = "Start timestamp is required")
    private OffsetDateTime startAt;

    @Schema(description = "Campaign End Timestamp")
    @NotNull(message = "End timestamp is required")
    private OffsetDateTime endAt;

    @Schema(description = "Published flag", example = "true")
    private boolean published = true;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getAccentColor() {
        return accentColor;
    }

    public void setAccentColor(String accentColor) {
        this.accentColor = accentColor;
    }

    public OffsetDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(OffsetDateTime startAt) {
        this.startAt = startAt;
    }

    public OffsetDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(OffsetDateTime endAt) {
        this.endAt = endAt;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
