package com.example.shopapppro.controller;



import com.example.shopapppro.dtos.CategoryDTO;
import com.example.shopapppro.models.Category;
import com.example.shopapppro.service.category.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.FieldError;



import java.util.List;

@RestController
@RequestMapping("${api.prefix}/categories")
//DI(dependence ịnecttion) tiêm thằng service vào controller
@RequiredArgsConstructor
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    //nếu tahm số trutyeenf vào là 1 đối tượng: request object
    @PostMapping("")
    public ResponseEntity<Object> createCategories(
            @Valid @RequestBody CategoryDTO categoriesDto,
            BindingResult result) {
        if (result.hasErrors()) {
            // Lấy danh sách các lỗi
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();

            // Trả về danh sách lỗi
            return ResponseEntity.badRequest().body(errorMessages);
        }
        categoryService.createCategory(categoriesDto);
        return ResponseEntity.ok("Inserted:  successfully" );
    }



//    hiển thị tất cả sản phẩm  dùng Getmapping
    @GetMapping("")//http://localhost:8089/api/v1/categories?page=1&limit=10
    public ResponseEntity<List<Category>> getallcategory(  @RequestParam("page") int page,
                                                   @RequestParam("limit") int limit){
        List<Category> categories= categoryService.getAllCategories();
        return  ResponseEntity.ok(categories);
    }



    @PutMapping("/{id}")
    public ResponseEntity<String> updatecategories( @PathVariable long id,
                                                    @Valid @RequestBody CategoryDTO categoryDTO){
        categoryService.updateCategory(id,categoryDTO);
        return  ResponseEntity.ok(" update thành công");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletecategories(@PathVariable long id){
        categoryService.deleteCategory(id);
        return  ResponseEntity.ok("delete with id:"+id+"ok");
    }

}
