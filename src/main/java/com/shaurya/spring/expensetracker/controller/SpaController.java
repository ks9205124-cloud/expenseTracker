package com.shaurya.spring.expensetracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping({"/login", "/register", "/callback", "/dashboard"})
    public String forwardToSpa() {
        // Forwards the route to index.html so React Router handles it client-side
        return "forward:/index.html";
    }
}