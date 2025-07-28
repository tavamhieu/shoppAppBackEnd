package com.example.shopapppro.repository;

import com.example.shopapppro.models.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage,Long> {
    List<ProductImage> findByProductId (Long productId);
}
