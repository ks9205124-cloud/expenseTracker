package com.shaurya.spring.expensetracker;

import com.shaurya.spring.expensetracker.security.AuthenticationLoggingFilter;
import com.shaurya.spring.expensetracker.security.CustomAuthenticationProvider;
import com.shaurya.spring.expensetracker.security.RequestLoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class WebAuthorizationConfig {

    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final SecurityBeansConfig securityBeansConfig;

    @Autowired
    public WebAuthorizationConfig(CustomAuthenticationProvider customAuthenticationProvider,
                                  SecurityBeansConfig securityBeansConfig) {
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.securityBeansConfig = securityBeansConfig;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000",
                "https://expense-tracker-backend-55wg.onrender.com"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // --- ORDER 1: OAuth2 Authorization Server Chain ---
    @Bean
    @Order(1)
    public SecurityFilterChain asFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        http.exceptionHandling((e) ->
                e.authenticationEntryPoint(
                        new LoginUrlAuthenticationEntryPoint("/login")
                ));

        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
        );

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    // --- ORDER 2: Standard Application Chain ---
    @Bean
    @Order(2)
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
        );

        // --- BACKEND LOGOUT CONFIGURATION ---
        http.logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/login")
        );

        http.httpBasic(Customizer.withDefaults());

        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(securityBeansConfig.jwtAuthenticationConverter()))
        );

        http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
                .ignoringRequestMatchers("/register")
                .ignoringRequestMatchers("/login")
                .ignoringRequestMatchers("/logout")
        );

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http.authenticationProvider(customAuthenticationProvider);

        http.addFilterBefore(
                new RequestLoggingFilter(), BasicAuthenticationFilter.class
        );
        http.addFilterAfter(
                new AuthenticationLoggingFilter(), BasicAuthenticationFilter.class
        );

        http.authorizeHttpRequests(c -> c
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                // Permit React SPA entry points and static files to prevent redirect loops
                .requestMatchers("/", "/index.html", "/static/**", "/assets/**", "*.js", "*.css", "*.ico", "/favicon.ico").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/register").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/logout").permitAll()
                .requestMatchers("/api/health").permitAll()
                .requestMatchers("/admin").hasRole("ADMIN")
                .requestMatchers("/user").hasAnyRole("USER","ADMIN")
                .requestMatchers("/api/**").hasAnyRole("USER","ADMIN")
                .anyRequest().authenticated()
        );

        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(
            JdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder,
            @org.springframework.beans.factory.annotation.Value("${oauth2.client.secret:my-local-secret}") String rawClientSecret
    ) {
        JdbcRegisteredClientRepository repository = new JdbcRegisteredClientRepository(jdbcTemplate);

        if (repository.findByClientId("client") == null) {
            RegisteredClient registeredClient = RegisteredClient
                    .withId(UUID.randomUUID().toString())
                    .clientId("client")
                    .clientSecret(passwordEncoder.encode(rawClientSecret))
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .redirectUri("http://localhost:5173/callback")
                    .redirectUri("https://expense-tracker-backend-55wg.onrender.com/callback")
                    .scope(OidcScopes.OPENID)
                    .tokenSettings(TokenSettings.builder()
                            .authorizationCodeTimeToLive(Duration.ofMinutes(3))
                            .build())
                    .clientSettings(ClientSettings.builder()
                            .requireProofKey(true)
                            .build())
                    .build();

            repository.save(registeredClient);
        }

        return repository;
    }
}