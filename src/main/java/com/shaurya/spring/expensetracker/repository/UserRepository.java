package com.shaurya.spring.expensetracker.repository;

import com.shaurya.spring.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.authorities WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String username);
}
