package com.shaurya.spring.expensetracker;

import com.shaurya.spring.expensetracker.security.JpaUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password4j.ScryptPassword4jPasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.HashMap;

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
        HashMap<String,PasswordEncoder> encoders = new HashMap<>();

        encoders.put("noop", NoOpPasswordEncoder.getInstance());        // noop kept for legacy/test rows created before Bcrypt was introduced;
        encoders.put("bcrypt", new BCryptPasswordEncoder(14));  // bcrypt (strength 14) is the default encoder for all new registrations

        return new DelegatingPasswordEncoder("bcrypt", encoders);   //DelegatingPasswordEncoder to allow existing user via noOP auth and new user via Bcrypt auth
    }
}
