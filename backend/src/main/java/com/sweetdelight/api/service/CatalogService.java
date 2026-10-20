package com.sweetdelight.api.service;

import com.sweetdelight.api.dto.*;
import com.sweetdelight.api.entity.Category;
import com.sweetdelight.api.entity.Product;
import com.sweetdelight.api.entity.ProductVariant;
import com.sweetdelight.api.exception.ResourceNotFoundException;
import com.sweetdelight.api.repository.CategoryRepository;
import com.sweetdelight.api.repository.ProductRepository;
import com.sweetdelight.api.repository.ProductVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    public CatalogService(CategoryRepository categoryRepository,
                          ProductRepository productRepository,
                          ProductVariantRepository productVariantRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
    }

    public List<CategoryDto> getCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapCategoryToDto)
                .collect(Collectors.toList());
    }

    public List<ProductDto> getProducts(Long categoryId, boolean inStockOnly) {
        List<Product> products = productRepository.findFiltered(categoryId, inStockOnly);
        return products.stream()
                .map(this::mapProductToDto)
                .collect(Collectors.toList());
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapProductToDto(product);
    }

    @Transactional
    public ProductDto createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        product.setInStock(request.isInStock());

        if (request.getVariants() != null) {
            for (CreateVariantRequest vReq : request.getVariants()) {
                ProductVariant variant = new ProductVariant();
                variant.setTitle(vReq.getTitle());
                variant.setPriceInr(vReq.getPriceInr());
                variant.setInStock(vReq.isInStock());
                product.addVariant(variant);
            }
        }

        Product saved = productRepository.save(product);
        return mapProductToDto(saved);
    }

    @Transactional
    public ProductDto updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        product.setInStock(request.isInStock());

        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            product.getVariants().clear();
            for (CreateVariantRequest vReq : request.getVariants()) {
                ProductVariant variant = new ProductVariant();
                variant.setTitle(vReq.getTitle());
                variant.setPriceInr(vReq.getPriceInr());
                variant.setInStock(vReq.isInStock());
                product.addVariant(variant);
            }
        }

        Product saved = productRepository.save(product);
        return mapProductToDto(saved);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    @Transactional
    public ProductDto updateStock(Long id, boolean inStock) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setInStock(inStock);
        Product saved = productRepository.save(product);
        return mapProductToDto(saved);
    }

    public ProductDto mapProductToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setImageUrl(product.getImageUrl());
        dto.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        dto.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
        dto.setInStock(product.isInStock());
        dto.setCreatedAt(product.getCreatedAt());

        if (product.getVariants() != null) {
            dto.setVariants(product.getVariants().stream()
                    .map(v -> new ProductVariantDto(v.getId(), v.getTitle(), v.getPriceInr(), v.isInStock()))
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private CategoryDto mapCategoryToDto(Category category) {
        return new CategoryDto(category.getId(), category.getName(), category.getSlug(), category.getDescription());
    }
}
