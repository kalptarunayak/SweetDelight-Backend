package com.sweetdelight.api.repository;

import com.sweetdelight.api.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    @Query("SELECT b FROM Banner b WHERE b.published = true AND b.startAt <= :now AND b.endAt >= :now ORDER BY b.createdAt DESC")
    List<Banner> findActiveBanners(@Param("now") OffsetDateTime now);

    Optional<Banner> findByCouponCodeIgnoreCase(String couponCode);

    @Query("SELECT b FROM Banner b WHERE UPPER(b.couponCode) = UPPER(:couponCode) AND b.published = true AND b.startAt <= :now AND b.endAt >= :now")
    Optional<Banner> findValidCouponBanner(@Param("couponCode") String couponCode, @Param("now") OffsetDateTime now);
}
