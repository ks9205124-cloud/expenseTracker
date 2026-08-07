package com.shaurya.spring.expensetracker.service;

import com.shaurya.spring.expensetracker.model.Category;
import com.shaurya.spring.expensetracker.model.User;
import com.shaurya.spring.expensetracker.repository.CategoryRepository;
import com.shaurya.spring.expensetracker.repository.ExpenseRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final UserService userService;

    public CategoryService(CategoryRepository categoryRepository, ExpenseRepository expenseRepository, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.expenseRepository = expenseRepository;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Category createCategory(String categoryName) {
        Category category = new Category();
        category.setName(categoryName);
        category.setUser(userService.getCurrentUser());
        return categoryRepository.save(category);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<Category> getCategoriesForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return categoryRepository.findByUserId(currentUser.getId());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getUser().getId().equals(userService.getCurrentUser().getId()) && !userService.isCurrentUserAdmin()) {
            throw new AccessDeniedException("User not allowed to delete category");
        }

        if (expenseRepository.existsByCategory(category)) {
            throw new IllegalStateException("Cannot delete category as expenses with this category exist");
        }

        categoryRepository.delete(category);
    }
}