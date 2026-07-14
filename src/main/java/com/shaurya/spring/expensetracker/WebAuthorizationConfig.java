package com.shaurya.spring.expensetracker;

import com.shaurya.spring.expensetracker.security.CustomAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebAuthorizationConfig {
    private final CustomAuthenticationProvider customAuthenticationProvider;
    @Autowired
    public WebAuthorizationConfig(CustomAuthenticationProvider customAuthenticationProvider) {
        this.customAuthenticationProvider = customAuthenticationProvider;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { //interface to implement a contract to set a filter chain to allow varied lvl of authentication powers
        http.httpBasic(Customizer.withDefaults());      //Customizer must be passed in a securityFilterChain

        http.authenticationProvider(customAuthenticationProvider);  //set authProvider to a customAuthProvider
        http.authorizeHttpRequests(C -> C.anyRequest().authenticated());  //allows every authority excepted by the customAuthenticationProvider to perform every task a proof of concept
        return http.build();
    }
}
