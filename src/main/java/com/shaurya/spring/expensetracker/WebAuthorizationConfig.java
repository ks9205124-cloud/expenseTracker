package com.shaurya.spring.expensetracker;

import com.shaurya.spring.expensetracker.security.AuthenticationLoggingFilter;
import com.shaurya.spring.expensetracker.security.CoustomAuthenticationSuccessHandler;
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
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class WebAuthorizationConfig {

    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final CoustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    public WebAuthorizationConfig(CustomAuthenticationProvider customAuthenticationProvider,
                                  CoustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
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

        // ENABLE form login in Order 1 so it handles human user login redirects cleanly
        http.formLogin(Customizer.withDefaults());

        return http.build();
    }

    // --- ORDER 2: Standard Application Chain ---
    @Bean
    @Order(2)
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.formLogin(Customizer.withDefaults());
        http.httpBasic(Customizer.withDefaults());

        // Add this line so Spring validates incoming Bearer JWT tokens on /api/** endpoints
        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
        );

        http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
                .ignoringRequestMatchers("/createUser")
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
                .requestMatchers("/error").permitAll()
                .requestMatchers("/createUser").permitAll()
                .requestMatchers("/admin").hasRole("ADMIN")
                .requestMatchers("/user").hasAnyRole("USER","ADMIN")
                .requestMatchers("/api/**").hasAnyRole("USER","ADMIN")
                .anyRequest().authenticated()
        );

        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        JdbcRegisteredClientRepository repository = new JdbcRegisteredClientRepository(jdbcTemplate);

        // Initializer check: Save default client into MySQL if it isn't present yet
        if (repository.findByClientId("client") == null) {
            RegisteredClient registeredClient = RegisteredClient
                    .withId(UUID.randomUUID().toString())
                    .clientId("client")
                    .clientSecret("{noop}secret")
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .redirectUri("https://oauth.pstmn.io/v1/callback")
                    .scope(OidcScopes.OPENID)
                    .tokenSettings(TokenSettings.builder()
                            .authorizationCodeTimeToLive(Duration.ofMinutes(30))
                            .build())
                    .clientSettings(ClientSettings.builder()
                            .requireProofKey(true)
                            .build())
                    .build();

            repository.save(registeredClient);
        }

        return repository;
    }
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // Tells Spring to read authorities from the "authorities" claim instead of "scope"
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        // Leaves authority names as-is (e.g., ROLE_USER, ROLE_ADMIN)
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}