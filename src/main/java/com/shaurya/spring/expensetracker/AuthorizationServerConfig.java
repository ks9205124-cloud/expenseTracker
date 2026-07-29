package com.shaurya.spring.expensetracker;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.shaurya.spring.expensetracker.repository.RsaKeyRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class AuthorizationServerConfig {

    private final RsaKeyRepository rsaKeyRepository;

    public AuthorizationServerConfig(RsaKeyRepository rsaKeyRepository) {
        this.rsaKeyRepository = rsaKeyRepository;
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        // Load the persistent key pair from MySQL
        RsaKeyRepository.PersistedRsaKey activeKey = rsaKeyRepository.findActiveKey();

        RSAKey rsaKey = new RSAKey.Builder(activeKey.getPublicKey())
                .privateKey(activeKey.getPrivateKey())
                .keyID(activeKey.getId())
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return (context) -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                Authentication principal = context.getPrincipal();

                Set<String> authorities = principal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet());

                context.getClaims().claims((claims) -> {
                    claims.put("authorities", authorities);
                });
            }
        };
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }
}