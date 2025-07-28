package com.example.shopapppro.service.product;

import com.example.shopapppro.dtos.ProductDTO;
import com.example.shopapppro.dtos.ProductImageDTO;
import com.example.shopapppro.models.Product;
import com.example.shopapppro.models.ProductImage;
import com.example.shopapppro.responces.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws Exception;

    Product getProductById(long id) throws Exception;

//    Page<Product> getAllProducts(String keyword,
//                                 Long categoryId, PageRequest pageRequest);

    Product updateProduct(long id, ProductDTO productDTO) throws Exception;

    void deleteProduct(long id);

    boolean existsByName(String name);

    ProductImage crateProductImage(
            long productId, ProductImageDTO productImageDTO) throws Exception;

    Page<ProductResponse> getAllProducts(PageRequest pageRequest);
}
