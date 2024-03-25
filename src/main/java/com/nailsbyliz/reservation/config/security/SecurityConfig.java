package com.nailsbyliz.reservation.config.security;

import java.net.URL;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class SecurityConfig {

    long ttl = 60 * 60 * 1000; // 1 hour
    long refreshTimeout = 60 * 1000; // 1 minute

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        // Create and configure your JWKSource using JWKSourceBuilder
        return JWKSourceBuilder.create(new URL("https://example.com/jwks.json"))
                .cache(ttl, refreshTimeout)
                .build();
    }
}
