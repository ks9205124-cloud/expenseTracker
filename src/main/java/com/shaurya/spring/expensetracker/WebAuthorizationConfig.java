package com.shaurya.spring.expensetracker;

import com.shaurya.spring.expensetracker.security.AuthenticationLoggingFilter;
import com.shaurya.spring.expensetracker.security.CoustomAuthenticationSuccessHandler;
import com.shaurya.spring.expensetracker.security.CustomAuthenticationProvider;
import com.shaurya.spring.expensetracker.security.RequestLoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebAuthorizationConfig {
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final CoustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @Autowired
    public WebAuthorizationConfig(CustomAuthenticationProvider customAuthenticationProvider, CoustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { //interface to implement a contract to set a filter chain to allow varied lvl of authentication powers
        http.formLogin(c -> c.successHandler(customAuthenticationSuccessHandler));      //custom succesHandler and spring default login logout pages
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/createUser")
        );
        http.authenticationProvider(customAuthenticationProvider);  //set authProvider to a customAuthProvider

        http.addFilterBefore(
                new RequestLoggingFilter(), BasicAuthenticationFilter.class     //add a proof of concept filter before custom Authentication
        );
        http.addFilterAfter(
                new AuthenticationLoggingFilter(), BasicAuthenticationFilter.class      //add a proof of concept filter after custom Authentication
        );

        http.authorizeHttpRequests(C -> C
                .requestMatchers("/createUser").permitAll()
                .anyRequest().authenticated());  //allows every authority excepted by the customAuthenticationProvider to perform every task a proof of concept
        return http.build();
    }
}
