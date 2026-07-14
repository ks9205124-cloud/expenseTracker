package com.shaurya.spring.expensetracker.service;

import com.shaurya.spring.expensetracker.model.User;
import com.shaurya.spring.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public void save(){
        User user = new User();
        user.setEmail("sample");
        user.setPassword("sample");
        user.setName("sample");
        userRepository.save(user);
    }
}
