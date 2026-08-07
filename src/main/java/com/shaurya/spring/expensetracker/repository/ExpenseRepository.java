package com.shaurya.spring.expensetracker.repository;

import com.shaurya.spring.expensetracker.model.Category;
import com.shaurya.spring.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // 1. Joins & fetches Category so React gets category names in a single SQL query
    @Query("SELECT e FROM Expense e JOIN FETCH e.category WHERE e.user.id = :userId")
    List<Expense> findByUserId(@Param("userId") Long userId);

    // 2. Fetch expenses by category ID
    @Query("SELECT e FROM Expense e JOIN FETCH e.category WHERE e.category.id = :categoryId")
    List<Expense> findByCategoryId(@Param("categoryId") Long categoryId);

    // 3. Spring Data JPA auto-generates this existence check natively
    boolean existsByCategory(Category category);
}