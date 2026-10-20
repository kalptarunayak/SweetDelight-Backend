package com.sweetdelight.api.repository;

import com.sweetdelight.api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants LEFT JOIN FETCH p.category WHERE (:categoryId IS NULL OR p.category.id = :categoryId) AND (:inStockOnly = false OR p.inStock = true)")
    List<Product> findFiltered(@Param("categoryId") Long categoryId, @Param("inStockOnly") boolean inStockOnly);

    List<Product> findByCategoryId(Long categoryId);
}
