package com.example.shopapppro.controller;

import com.example.shopapppro.dtos.ProductDTO;
import com.example.shopapppro.dtos.ProductImageDTO;
import com.example.shopapppro.models.Product;
import com.example.shopapppro.models.ProductImage;
import com.example.shopapppro.repository.ProductRepository;
import com.example.shopapppro.responces.ProductListResponse;
import com.example.shopapppro.responces.ProductResponse;
import com.example.shopapppro.service.product.IProductService;

import com.example.shopapppro.service.product.ProductService;
import com.github.javafaker.Faker;
import jakarta.validation.Path;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.Binding;
import java.io.IOException;
import java.nio.channels.MulticastChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
//    private final IProductSevice productService;
    private final IProductService productService; // Thay vì IProductSevice

    @PostMapping(value = "")
    public ResponseEntity<?> createProduct(@RequestBody @Valid ProductDTO productDTO,
                                           //@ModelAttribute("files")  List<MultipartFile> files,
                                           BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Product newProduct =productService.createProduct(productDTO);

            return ResponseEntity.ok(newProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "uploads/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    //POST http://localhost:8088/v1/api/products
    public ResponseEntity<?> uploadImages(
            @PathVariable("id") Long productId,
            @RequestPart("files") List<MultipartFile> files // Thay đổi ở đây
            //@ModelAttribute("files") List<MultipartFile> files
    ) {
        try {
            Product existingProduct = productService.getProductById(productId);
            files = files == null ? new ArrayList<MultipartFile>() : files;
            List<ProductImage> productImages = new ArrayList<>();
            if (files == null || files.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select at least one file");
            }
            for (MultipartFile file : files) {
                if (files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
                    return ResponseEntity.badRequest().body("you can only upload maximum 5 img");

                }

//                 Dùng trực tiếp biến 'files'
                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("File quá lớn, giới hạn là 10MB");
                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("File phải là định dạng ảnh");
                }
                // Lưu file và cập nhật thumbnail trong DTO
                //String filename = productService.storeFile(file); // Thay thế hàm này với code của bạn để
                // lưu file
                //lưu vào đối tượng product trong
                String filename = storeFile(file);
                ProductImage productImage = productService.crateProductImage(
                        existingProduct.getId(),
                        ProductImageDTO.builder()
                                .imageUrl(filename)
                                .build()
                );
                productImages.add(productImage);
            }

            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    private  boolean isImageFile(MultipartFile file){
        String contentType = file.getContentType();
        return  contentType !=  null && contentType.startsWith("image/");
    }

    private  String storeFile(MultipartFile file) throws IOException {
        if(!isImageFile(file) || file.getOriginalFilename() == null){
            throw  new IOException("Invalid img format ok");
        }
        String filename= StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uniqueFilename= UUID.randomUUID().toString()+"_"+filename;
        java.nio.file.Path  uploadDir= Paths.get("uploads");
        if(!Files.exists(uploadDir)){
            Files.createDirectories(uploadDir);
        }

        java.nio.file.Path destination =Paths.get(uploadDir.toString(),uniqueFilename);
        Files.copy(file.getInputStream(),destination, StandardCopyOption.REPLACE_EXISTING);
        return  uniqueFilename;
    }

    @GetMapping("")
    public ResponseEntity<ProductListResponse> getProducts(
//            @RequestParam(defaultValue = "") String keyword,
//            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit

    ){
        PageRequest pageRequest= PageRequest.of(
                page,limit,
                Sort.by("createdAt").descending());
        Page<ProductResponse> productPage= productService.getAllProducts(pageRequest);
//        lấy tổng số trảng
        int totalPage = productPage.getTotalPages();
        List<ProductResponse>products =productPage.getContent();
        return ResponseEntity.ok(ProductListResponse
                .builder()
                .products(products)
                .totalPages(totalPage)
                .build());
    }

   @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(
            @PathVariable("id") Long productId
    ) {
       try {
           Product existingProduct = productService.getProductById(productId);

           return  ResponseEntity.ok(ProductResponse.fromProduct(existingProduct));

       } catch (Exception e) {
          return  ResponseEntity.badRequest().body(e.getMessage());
       }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable long id){

        try {
            productService.deleteProduct(id);
            return  ResponseEntity.status(HttpStatus.OK).body(String.format("dẻ lẻ  tẻ id=%d",id));

        } catch (Exception e) {
            return  ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable long id,
            @RequestBody ProductDTO productDTO) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
//    @PostMapping("/generateFakeProducts") hàm tạo dữ liệu giả  phải tắt
//    public  ResponseEntity<String> generateProducts(){
//        Faker faker= new Faker();
//        for (int i= 0;i<1_000;i++){
//            String productName = faker.commerce().productName();
//            if(productService.existsByName(productName)){
//                continue;
//            }
//            ProductDTO productDTO= ProductDTO.builder()
//                    .name(productName)
//                    .price((float)faker.number().numberBetween(10,90_000_000))
//                    .description(faker.lorem().sentence())
//                    .thumbnail("")
//                    .categoryId((long)faker.number().numberBetween(2,8))
//                    .build();
//            try {
//                productService.createProduct(productDTO);
//            } catch (Exception e) {
//               return ResponseEntity.badRequest().body(e.getMessage());
//            }
//        }
//        return ResponseEntity.ok("fake Product created successfully");
//    }
//}
