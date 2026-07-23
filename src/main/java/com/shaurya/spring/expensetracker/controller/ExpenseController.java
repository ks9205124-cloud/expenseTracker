package com.shaurya.spring.expensetracker.controller;

import com.shaurya.spring.expensetracker.dto.CreateExpenseRequest;
import com.shaurya.spring.expensetracker.model.Expense;
import com.shaurya.spring.expensetracker.service.ExpenseService;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public Expense addExpense(@RequestBody CreateExpenseRequest expenseRequest) {
        return expenseService.createExpense(expenseRequest.amount(), expenseRequest.date(), expenseRequest.categoryId());
    }

    @GetMapping
    public List<Expense> getExpensesForCurrentUser() {
        return expenseService.getExpensesForCurrentUser();
    }

    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable Long id) {
        return expenseService.getExpenseById(id);
    }

    @DeleteMapping("/{expenseId}")
    public void deleteExpenseById(@PathVariable Long expenseId) {
        expenseService.deleteExpense(expenseId);
    }
}
