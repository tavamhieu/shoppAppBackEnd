package com.example.shopapppro.service.category;

import com.example.shopapppro.dtos.CategoryDTO;
import com.example.shopapppro.models.Category;
import com.example.shopapppro.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        Category newCategory = Category
                .builder()
                .name(categoryDTO.getName())
                .build();
        return categoryRepository.save(newCategory);
    }


    @Override
    public Category getCategoryById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("category not found"));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(long categoryId, CategoryDTO categoriesDto) {
        Category existingCategory = getCategoryById(categoryId);// bước 1 tìm ra category với id= categoryId

        existingCategory.setName(categoriesDto.getName()); // bước 2 sauwr dổi dungf method set và get
        categoryRepository.save(existingCategory);
        return  existingCategory;
    }

    @Override
    public void deleteCategory(long id)  {
        categoryRepository.deleteById(id);

    }
}
