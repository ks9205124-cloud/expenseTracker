package com.shaurya.spring.expensetracker.repository;

import com.shaurya.spring.expensetracker.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Spring Data JPA generates this automatically filtering by user.id
    List<Category> findByUserId(Long userId);
}