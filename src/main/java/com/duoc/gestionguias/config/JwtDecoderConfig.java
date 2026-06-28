package com.duoc.gestionguias.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class JwtDecoderConfig {

    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}") String issuerUri,
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:}") String jwkSetUri,
            @Value("${app.security.jwt.audience:}") String audience,
            @Value("${app.security.jwt.local-secret}") String localSecret
    ) {
        if (jwkSetUri != null && !jwkSetUri.isBlank()) {
            NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
            decoder.setJwtValidator(jwtValidator(issuerUri, audience));
            return decoder;
        }

        if (issuerUri != null && !issuerUri.isBlank()) {
            return JwtDecoders.fromIssuerLocation(issuerUri);
        }

        SecretKeySpec secretKey = new SecretKeySpec(
                localSecret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    private OAuth2TokenValidator<Jwt> jwtValidator(String issuerUri, String audience) {
        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        validators.add(JwtValidators.createDefault());

        if (issuerUri != null && !issuerUri.isBlank()) {
            validators.add(new JwtIssuerValidator(issuerUri));
        }

        if (audience != null && !audience.isBlank()) {
            validators.add(jwt -> {
                if (jwt.getAudience().contains(audience)) {
                    return OAuth2TokenValidatorResult.success();
                }

                OAuth2Error error = new OAuth2Error(
                        "invalid_token",
                        "El token no contiene la audiencia esperada: " + audience,
                        null
                );

                return OAuth2TokenValidatorResult.failure(error);
            });
        }

        return new DelegatingOAuth2TokenValidator<>(validators);
    }
}