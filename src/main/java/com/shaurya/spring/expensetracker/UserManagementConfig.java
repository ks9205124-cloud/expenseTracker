package com.shaurya.spring.expensetracker;

import com.shaurya.spring.expensetracker.security.JpaUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserManagementConfig {

    private final JpaUserDetailsService userDetailsService;

    public UserManagementConfig(JpaUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;

    }


    @Bean   //interface to implement a contract setting user detail service
    UserDetailsService userDetailsService(){
        return userDetailsService;     //a custom Detail Service : role is to retrieve current users data
    }
    @Bean   //interface to implement a contract of encoding the password
    PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();   //no not perform any encryption (proof of concept)
    }
}
