package com.sweetdelight.api.service;

import com.sweetdelight.api.dto.BannerDto;
import com.sweetdelight.api.dto.CreateBannerRequest;
import com.sweetdelight.api.dto.UpdateBannerRequest;
import com.sweetdelight.api.entity.Banner;
import com.sweetdelight.api.exception.BadRequestException;
import com.sweetdelight.api.exception.ResourceNotFoundException;
import com.sweetdelight.api.repository.BannerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BannerService {

    private final BannerRepository bannerRepository;

    public BannerService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    public List<BannerDto> getActiveBanners() {
        return bannerRepository.findActiveBanners(OffsetDateTime.now()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<BannerDto> getAllBanners() {
        return bannerRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public BannerDto getBannerById(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found with id: " + id));
        return mapToDto(banner);
    }

    @Transactional
    public BannerDto createBanner(CreateBannerRequest request) {
        if (request.getEndAt().isBefore(request.getStartAt())) {
            throw new BadRequestException("Banner end date must be after start date");
        }

        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            String code = request.getCouponCode().trim().toUpperCase();
            if (bannerRepository.findByCouponCodeIgnoreCase(code).isPresent()) {
                throw new BadRequestException("Coupon code '" + code + "' already exists");
            }
        }

        Banner banner = new Banner();
        banner.setTitle(request.getTitle());
        banner.setSubtitle(request.getSubtitle());
        banner.setImageUrl(request.getImageUrl());
        banner.setDiscountPercent(request.getDiscountPercent());
        banner.setCouponCode(request.getCouponCode());
        banner.setAccentColor(request.getAccentColor());
        banner.setStartAt(request.getStartAt());
        banner.setEndAt(request.getEndAt());
        banner.setPublished(request.isPublished());

        Banner saved = bannerRepository.save(banner);
        return mapToDto(saved);
    }

    @Transactional
    public BannerDto updateBanner(Long id, UpdateBannerRequest request) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found with id: " + id));

        if (request.getEndAt().isBefore(request.getStartAt())) {
            throw new BadRequestException("Banner end date must be after start date");
        }

        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            String code = request.getCouponCode().trim().toUpperCase();
            Optional<Banner> existing = bannerRepository.findByCouponCodeIgnoreCase(code);
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new BadRequestException("Coupon code '" + code + "' is already in use by another banner");
            }
        }

        banner.setTitle(request.getTitle());
        banner.setSubtitle(request.getSubtitle());
        banner.setImageUrl(request.getImageUrl());
        banner.setDiscountPercent(request.getDiscountPercent());
        banner.setCouponCode(request.getCouponCode());
        banner.setAccentColor(request.getAccentColor());
        banner.setStartAt(request.getStartAt());
        banner.setEndAt(request.getEndAt());
        banner.setPublished(request.isPublished());

        Banner saved = bannerRepository.save(banner);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteBanner(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found with id: " + id));
        bannerRepository.delete(banner);
    }

    /**
     * Recomputes coupon discount from database. Returns discount in INR.
     */
    public BigDecimal calculateDiscount(String couponCode, BigDecimal subtotal) {
        if (couponCode == null || couponCode.isBlank() || subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        String code = couponCode.trim().toUpperCase();
        Optional<Banner> bannerOpt = bannerRepository.findValidCouponBanner(code, OffsetDateTime.now());

        if (bannerOpt.isEmpty()) {
            throw new BadRequestException("Invalid or expired coupon code: " + code);
        }

        Banner banner = bannerOpt.get();
        int percent = banner.getDiscountPercent();
        if (percent <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = subtotal.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Cap discount to subtotal
        return discount.min(subtotal);
    }

    public BannerDto mapToDto(Banner banner) {
        BannerDto dto = new BannerDto();
        dto.setId(banner.getId());
        dto.setTitle(banner.getTitle());
        dto.setSubtitle(banner.getSubtitle());
        dto.setImageUrl(banner.getImageUrl());
        dto.setDiscountPercent(banner.getDiscountPercent());
        dto.setCouponCode(banner.getCouponCode());
        dto.setAccentColor(banner.getAccentColor());
        dto.setStartAt(banner.getStartAt());
        dto.setEndAt(banner.getEndAt());
        dto.setPublished(banner.isPublished());
        return dto;
    }
}
