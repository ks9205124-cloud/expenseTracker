package com.shaurya.spring.expensetracker.repository;

import com.shaurya.spring.expensetracker.model.Category;
import com.shaurya.spring.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.user WHERE c.user = :user")
    List<Category> findByUser(User user);
}
