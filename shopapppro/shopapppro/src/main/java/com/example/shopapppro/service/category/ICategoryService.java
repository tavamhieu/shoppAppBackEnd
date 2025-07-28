package com.example.shopapppro.service.category;


import com.example.shopapppro.dtos.CategoryDTO;
import com.example.shopapppro.models.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoryDTO category);

    Category getCategoryById(long id);

    List<Category> getAllCategories();

    Category updateCategory(long categoryId, CategoryDTO category);

    void deleteCategory(long id) throws Exception;
}