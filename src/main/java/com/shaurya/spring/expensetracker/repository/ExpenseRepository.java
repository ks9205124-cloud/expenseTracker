package com.shaurya.spring.expensetracker.repository;

import com.shaurya.spring.expensetracker.model.Category;
import com.shaurya.spring.expensetracker.model.Expense;
import com.shaurya.spring.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    @Query("SELECT e FROM Expense e LEFT JOIN FETCH e.user WHERE e.user = :user")
    List<Expense> findByUser(User user);
    @Query("SELECT e FROM Expense e LEFT JOIN FETCH e.category WHERE e.category = :category")
    List<Expense> findByCategory(Category category);
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Expense e WHERE e.category = :category")
    boolean existsByCategory(Category category);

}
