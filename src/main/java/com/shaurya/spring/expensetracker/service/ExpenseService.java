package com.shaurya.spring.expensetracker.service;

import com.shaurya.spring.expensetracker.model.Category;
import com.shaurya.spring.expensetracker.model.Expense;
import com.shaurya.spring.expensetracker.model.User;
import com.shaurya.spring.expensetracker.repository.CategoryRepository;
import com.shaurya.spring.expensetracker.repository.ExpenseRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public ExpenseService(ExpenseRepository expenseRepository, CategoryRepository categoryRepository, UserService userService) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Expense createExpense(BigDecimal amount, LocalDate date, Long categoryId) {
        User currentUser = userService.getCurrentUser();

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getUser().getId().equals(currentUser.getId()) && !userService.isCurrentUserAdmin()) {
            throw new AccessDeniedException("You do not own this category");
        }

        Expense expense = new Expense();
        expense.setAmount(amount);
        expense.setDate(date);
        expense.setUser(currentUser);
        expense.setCategory(category);

        return expenseRepository.save(expense);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<Expense> getExpensesForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        // Uses the new query that fetches categories and filters explicitly by user ID
        return expenseRepository.findByUserId(currentUser.getId());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Expense getExpenseById(Long expenseId) {
        User currentUser = userService.getCurrentUser();

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(currentUser.getId()) && !userService.isCurrentUserAdmin()) {
            throw new AccessDeniedException("You do not own this expense");
        }

        return expense;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public void deleteExpense(Long expenseId) {
        User currentUser = userService.getCurrentUser();

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(currentUser.getId()) && !userService.isCurrentUserAdmin()) {
            throw new AccessDeniedException("You do not own this expense");
        }

        expenseRepository.delete(expense);
    }
}