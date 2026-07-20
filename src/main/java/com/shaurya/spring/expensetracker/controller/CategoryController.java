package com.shaurya.spring.expensetracker.controller;

import com.shaurya.spring.expensetracker.dto.CreateCategoryRequest;
import com.shaurya.spring.expensetracker.model.Category;
import com.shaurya.spring.expensetracker.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> getCategoriesForCurrentUser() {
        return  categoryService.getCategoriesForCurrentUser();
    }

    @DeleteMapping("/{id}")
    public void deleteCategoriesForCurrentUser(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }

    @PostMapping
    public Category createCategory(@Valid @RequestBody CreateCategoryRequest categoryRequest) {
        return categoryService.createCategory(categoryRequest.categoryName());
    }
}
