package com.shaurya.spring.expensetracker.service;

import com.shaurya.spring.expensetracker.exception.DuplicateResourceException;
import com.shaurya.spring.expensetracker.model.Authority;
import com.shaurya.spring.expensetracker.model.User;
import com.shaurya.spring.expensetracker.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    public boolean isCurrentUserAdmin() {
        return getCurrentUser().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public User save(String username, String password) {
        // Check if the user/email already exists before saving
        if (userRepository.existsByEmail(username)) {
            throw new DuplicateResourceException("A user with email '" + username + "' already exists.");
        }

        User user = new User();
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(username);
        Authority authority = new Authority();
        authority.setAuthority("ROLE_USER");
        authority.setUser(user);
        user.setAuthorities(List.of(authority));
        return userRepository.save(user);
    }
}