package com.example.shopapppro.service.product;

import com.example.shopapppro.dtos.ProductDTO;
import com.example.shopapppro.dtos.ProductImageDTO;
import com.example.shopapppro.exception.DataNotFoundException;
import com.example.shopapppro.exception.InvalidParamException;
import com.example.shopapppro.models.Category;
import com.example.shopapppro.models.Product;
import com.example.shopapppro.models.ProductImage;
import com.example.shopapppro.repository.CategoryRepository;
import com.example.shopapppro.repository.ProductImageRepository;
import com.example.shopapppro.repository.ProductRepository;
import com.example.shopapppro.responces.ProductResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.lang.annotation.Retention;
import java.util.Optional;

import static com.example.shopapppro.models.ProductImage.MAXIMUM_IMAGES_PER_PRODUCT;

@Service
@RequiredArgsConstructor
public class ProductService  implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private  final ProductImageRepository productImageRepository;

//    @Override
//    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
////        kiểm tr xem tk category này có tồn tại chưa
//        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
//                .orElseThrow(() -> new DataNotFoundException("Cannot find category with id" + productDTO.getCategoryId()));
//        Product newProduct = Product.builder()
//                .name(productDTO.getName())
//                .price(productDTO.getPrice())
//                .description(productDTO.getDescription())
//                .thumbnail(productDTO.getThumbnail())
//                .category(existingCategory)
//                .build();
//        return productRepository.save(newProduct);
//    }
    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {

        Category existingCategory = categoryRepository
                .findById(productDTO.getCategoryId())
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Cannot find category with id: " + productDTO.getCategoryId()));

        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long productId) throws Exception {
        return productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find product with id" + productId));
    }

    @Override
//    public Page<Product> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest) {
    public Page<ProductResponse> getAllProducts(PageRequest pageRequest) {
        return productRepository.findAll(pageRequest).map(ProductResponse::fromProduct);
    }

//    @Override
//    public Product updateProduct(
//            long id, ProductDTO productDTO) throws Exception {
//        Product existingProduct = getProductById(id);
//        if (existingProduct != null) {
//            Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
//                    .orElseThrow(() -> new DataNotFoundException("Cannot find category with id" + productDTO.getCategoryId()));
//            existingProduct.setName(productDTO.getName());
//            existingProduct.setDescription(productDTO.getDescription());
//            existingProduct.setCategory(existingCategory);
//            existingProduct.setPrice(productDTO.getPrice());
//            existingProduct.setThumbnail(productDTO.getThumbnail());
//            return productRepository.save(existingProduct);
//        }
//        return null;
//    }
@Override
@Transactional
public Product updateProduct(
        long id,
        ProductDTO productDTO
)
        throws Exception {
    Product existingProduct = getProductById(id);
    if (existingProduct != null) {
        //copy các thuộc tính từ DTO -> Product
        //Có thể sử dụng ModelMapper
        Category existingCategory = categoryRepository
                .findById(productDTO.getCategoryId())
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Cannot find category with id: " + productDTO.getCategoryId()));
        if (productDTO.getName() != null && !productDTO.getName().isEmpty()) {
            existingProduct.setName(productDTO.getName());
        }

        existingProduct.setCategory(existingCategory);
        if (productDTO.getPrice() >= 0) {
            existingProduct.setPrice(productDTO.getPrice());
        }
        if (productDTO.getDescription() != null &&
                !productDTO.getDescription().isEmpty()) {
            existingProduct.setDescription(productDTO.getDescription());
        }
        if (productDTO.getThumbnail() != null &&
                !productDTO.getThumbnail().isEmpty()) {
            existingProduct.setThumbnail(productDTO.getThumbnail());
        }
        return productRepository.save(existingProduct);
    }
    return null;
}

    @Override
    public void deleteProduct(long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct.ifPresent(productRepository::delete);

    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }
    @Override
    public ProductImage crateProductImage(
            long productId, ProductImageDTO productImageDTO) throws Exception{
        Product existingProduct = productRepository
                .findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find category with id" + productImageDTO.getProductId()));
        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
//        ko tạo quá số ahr cho 1 sản phẩm
        int size= productImageRepository.findByProductId(productId).size();
        if(size>=MAXIMUM_IMAGES_PER_PRODUCT){
            throw new InvalidParamException("number of img must be <="+ MAXIMUM_IMAGES_PER_PRODUCT);
        }
        return productImageRepository.save(newProductImage);
    }

}
