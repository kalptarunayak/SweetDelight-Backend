package com.sweetdelight.api.service;

import com.sweetdelight.api.entity.Banner;
import com.sweetdelight.api.exception.BadRequestException;
import com.sweetdelight.api.repository.BannerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponDiscountTest {

    @Mock
    private BannerRepository bannerRepository;

    private BannerService bannerService;

    @BeforeEach
    void setUp() {
        bannerService = new BannerService(bannerRepository);
    }

    @Test
    @DisplayName("Should apply valid coupon discount correctly (15% on ₹1000 = ₹150)")
    void testValidCouponDiscount() {
        Banner banner = new Banner();
        banner.setId(1L);
        banner.setCouponCode("SWEET15");
        banner.setDiscountPercent(15);
        banner.setPublished(true);
        banner.setStartAt(OffsetDateTime.now().minusDays(1));
        banner.setEndAt(OffsetDateTime.now().plusDays(30));

        when(bannerRepository.findValidCouponBanner(eq("SWEET15"), any(OffsetDateTime.class)))
                .thenReturn(Optional.of(banner));

        BigDecimal subtotal = new BigDecimal("1000.00");
        BigDecimal discount = bannerService.calculateDiscount("SWEET15", subtotal);

        assertEquals(new BigDecimal("150.00"), discount);
    }

    @Test
    @DisplayName("Should return 0 discount when coupon code is null or empty")
    void testNullOrEmptyCoupon() {
        BigDecimal subtotal = new BigDecimal("1000.00");
        assertEquals(BigDecimal.ZERO, bannerService.calculateDiscount(null, subtotal));
        assertEquals(BigDecimal.ZERO, bannerService.calculateDiscount("   ", subtotal));
    }

    @Test
    @DisplayName("Should throw BadRequestException when coupon code is not found or expired")
    void testInvalidOrExpiredCoupon() {
        when(bannerRepository.findValidCouponBanner(eq("EXPIRED99"), any(OffsetDateTime.class)))
                .thenReturn(Optional.empty());

        BigDecimal subtotal = new BigDecimal("500.00");
        assertThrows(BadRequestException.class, () -> bannerService.calculateDiscount("EXPIRED99", subtotal));
    }

    @Test
    @DisplayName("Should cap discount to subtotal if discount calculation exceeds subtotal")
    void testDiscountCappedToSubtotal() {
        Banner banner = new Banner();
        banner.setId(2L);
        banner.setCouponCode("FREE100");
        banner.setDiscountPercent(100);
        banner.setPublished(true);
        banner.setStartAt(OffsetDateTime.now().minusDays(1));
        banner.setEndAt(OffsetDateTime.now().plusDays(5));

        when(bannerRepository.findValidCouponBanner(eq("FREE100"), any(OffsetDateTime.class)))
                .thenReturn(Optional.of(banner));

        BigDecimal subtotal = new BigDecimal("750.00");
        BigDecimal discount = bannerService.calculateDiscount("FREE100", subtotal);

        assertEquals(new BigDecimal("750.00"), discount);
    }
}
