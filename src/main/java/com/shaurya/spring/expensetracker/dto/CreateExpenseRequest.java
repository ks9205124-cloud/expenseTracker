package com.shaurya.spring.expensetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateExpenseRequest(BigDecimal amount, LocalDate date, Long categoryId) {
}
