package com.shaurya.spring.expensetracker.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class CoustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        var authorities = authentication.getAuthorities();

        var auth = authorities.stream()
                .filter(a -> a.getAuthority().equals("ROLE_USER"))
                .findFirst();

        if (auth.isPresent()) {
            response.sendRedirect("/hello");
        }else{
            response.sendRedirect("/error");    //what to do if user does not have the correct authority
        }

    }
}
