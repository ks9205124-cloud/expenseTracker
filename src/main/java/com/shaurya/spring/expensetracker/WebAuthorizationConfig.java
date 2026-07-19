package com.shaurya.spring.expensetracker;

import com.shaurya.spring.expensetracker.security.AuthenticationLoggingFilter;
import com.shaurya.spring.expensetracker.security.CoustomAuthenticationSuccessHandler;
import com.shaurya.spring.expensetracker.security.CustomAuthenticationProvider;
import com.shaurya.spring.expensetracker.security.RequestLoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
    CorsConfigurationSource corsConfigurationSource() {     //implemented corsConfig for future upgrade to react frontend
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { //interface to implement a contract to set a filter chain to allow varied lvl of authentication powers
        http.formLogin(c -> c.successHandler(customAuthenticationSuccessHandler));      //custom succesHandler and spring default login logout pages

        http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/createUser")
        );

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http.authenticationProvider(customAuthenticationProvider);  //set authProvider to a customAuthProvider

        http.addFilterBefore(
                new RequestLoggingFilter(), BasicAuthenticationFilter.class     //add a proof of concept filter before custom Authentication
        );
        http.addFilterAfter(
                new AuthenticationLoggingFilter(), BasicAuthenticationFilter.class      //add a proof of concept filter after custom Authentication
        );

        http.authorizeHttpRequests(c -> c
                .requestMatchers("/createUser").permitAll()     //no role/authority required to access this page
                .requestMatchers("/admin").hasRole("ADMIN")     //allow only admin to access admin page
                .requestMatchers("/user").hasAnyRole("USER","ADMIN")   //allow both user and admin to access user page
                .anyRequest().authenticated()
        );
        return http.build();
    }
}