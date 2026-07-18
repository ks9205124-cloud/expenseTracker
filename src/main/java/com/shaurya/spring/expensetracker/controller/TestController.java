package com.shaurya.spring.expensetracker.controller;

import com.shaurya.spring.expensetracker.dto.CreateUserRequest;
import com.shaurya.spring.expensetracker.model.User;
import com.shaurya.spring.expensetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    private UserService userService;

    @GetMapping("/hello")
    public String hello(Authentication a) {
        return "Hello, " + a.getName() + "!";
    }
    @PostMapping("/createUser")
    public User createUser(@RequestBody CreateUserRequest userRequest) {
        System.out.println(userRequest.email() + " " + userRequest.password());
        return userService.save(userRequest.email(), userRequest.password());
    }
}
