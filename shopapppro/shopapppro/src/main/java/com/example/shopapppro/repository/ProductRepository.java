package com.example.shopapppro.repository;

import com.example.shopapppro.models.Category;
import com.example.shopapppro.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);

    Page<Product> findAll(Pageable pageable);//phân trang các sản phẩm khi lấy ra các trang hiên thị sản phẩm

    List<Product> findByCategory(Category category);
}
