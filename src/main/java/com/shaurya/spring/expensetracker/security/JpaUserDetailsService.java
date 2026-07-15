package com.shaurya.spring.expensetracker.security;


import com.shaurya.spring.expensetracker.model.User;
import com.shaurya.spring.expensetracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    public JpaUserDetailsService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =  userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Name Not Found"));

        return new SecurityUser(user);
    }
}
