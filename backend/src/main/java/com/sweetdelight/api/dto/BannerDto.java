package com.sweetdelight.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public class BannerDto {

    @Schema(description = "Banner ID", example = "1")
    private Long id;

    @Schema(description = "Title", example = "Grand Festive Sale")
    private String title;

    @Schema(description = "Subtitle", example = "Flat 15% OFF on cakes and chocolates")
    private String subtitle;

    @Schema(description = "Banner Image URL", example = "https://images.unsplash.com/photo-1535141192574-5d4897c13136?w=1200")
    private String imageUrl;

    @Schema(description = "Discount percentage", example = "15")
    private Integer discountPercent;

    @Schema(description = "Promotional Coupon Code", example = "SWEET15")
    private String couponCode;

    @Schema(description = "Hex Accent Color", example = "#E11D48")
    private String accentColor;

    @Schema(description = "Start Timestamp")
    private OffsetDateTime startAt;

    @Schema(description = "End Timestamp")
    private OffsetDateTime endAt;

    @Schema(description = "Publication flag", example = "true")
    private boolean published;

    public BannerDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
