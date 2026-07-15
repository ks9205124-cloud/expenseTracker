package com.shaurya.spring.expensetracker.service;

import com.shaurya.spring.expensetracker.model.Authority;
import com.shaurya.spring.expensetracker.model.User;
import com.shaurya.spring.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User save(String username, String password) {
        //TODO: handel if userName already exists
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
