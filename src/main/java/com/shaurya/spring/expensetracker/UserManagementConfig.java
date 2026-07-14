package com.shaurya.spring.expensetracker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserManagementConfig {
    @Bean   //interface to implement a contract setting user detail service
    UserDetailsService userDetailsService(){
        var user = User.withUsername("Shaurya")
                .password("1234")
                .authorities("read")
                .build();

        return new InMemoryUserDetailsManager(user);    //proof of concept instead of a custom method or jdbc
    }
    @Bean   //interface to implement a contract of encoding the password
    PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();   //no not perform any encryption (proof of concept)
    }
}
