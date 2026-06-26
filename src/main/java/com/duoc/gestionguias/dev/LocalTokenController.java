package com.duoc.gestionguias.dev;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@Profile("local")
public class LocalTokenController {

    private final JwtEncoder jwtEncoder;

    public LocalTokenController(@Value("${app.security.jwt.local-secret}") String localSecret) {
        SecretKeySpec secretKey = new SecretKeySpec(
                localSecret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        this.jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    @GetMapping("/local/token/gestor")
    public Map<String, String> tokenGestor() {
        return Map.of("token", generarToken("lucas-gestor", List.of("GESTOR_GUIAS")));
    }

    @GetMapping("/local/token/descarga")
    public Map<String, String> tokenDescarga() {
        return Map.of("token", generarToken("lucas-descarga", List.of("DESCARGA_GUIAS")));
    }

    private String generarToken(String subject, List<String> roles) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("local-dev")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(subject)
                .claim("roles", roles)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}