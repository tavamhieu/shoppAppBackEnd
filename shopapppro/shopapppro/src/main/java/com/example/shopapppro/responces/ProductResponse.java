package com.example.shopapppro.responces;

import com.example.shopapppro.models.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse extends BaseResponse {
//    private Long id;
    private String name;
    private Float price;
    private String thumbnail;
    private String description;
//    // Thêm trường totalPages
//    private int totalPages;

//    @JsonProperty("product_images")
//    private List<ProductImage> productImages = new ArrayList<>();

    @JsonProperty("category_id")
    private Long categoryId;

    public static ProductResponse fromProduct(Product product) {
        ProductResponse productResponse = ProductResponse.builder()
//                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
//                .productImages(product.getProductImages())
                .build();
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setUpdatedAt(product.getUpdatedAt());
        return productResponse;
    }
}