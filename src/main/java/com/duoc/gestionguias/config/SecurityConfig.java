package com.duoc.gestionguias.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String ROLE_DESCARGA_GUIAS = "DESCARGA_GUIAS";
    private static final String ROLE_GESTOR_GUIAS = "GESTOR_GUIAS";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/health",
                                "/actuator/health",
                                "/local/token",
                                "/local/token/**",
                                "/error"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/guias/descargar")
                        .hasRole(ROLE_DESCARGA_GUIAS)

                        .requestMatchers(HttpMethod.POST, "/api/guias/crear")
                        .hasRole(ROLE_GESTOR_GUIAS)

                        .requestMatchers(HttpMethod.POST, "/api/guias/subir")
                        .hasRole(ROLE_GESTOR_GUIAS)

                        .requestMatchers(HttpMethod.PUT, "/api/guias/actualizar")
                        .hasRole(ROLE_GESTOR_GUIAS)

                        .requestMatchers(HttpMethod.DELETE, "/api/guias/eliminar")
                        .hasRole(ROLE_GESTOR_GUIAS)

                        .requestMatchers(HttpMethod.GET, "/api/guias/historial")
                        .hasRole(ROLE_GESTOR_GUIAS)

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Set<GrantedAuthority> authorities = new HashSet<>();

            agregarRolesDesdeClaim(jwt, authorities, "roles");
            agregarRolesDesdeClaim(jwt, authorities, "extension_roles");
            agregarRolesDesdeClaim(jwt, authorities, "extension_Roles");
            agregarRolesDesdeClaim(jwt, authorities, "role");

            return authorities;
        });

        return converter;
    }

    private void agregarRolesDesdeClaim(
            Jwt jwt,
            Set<GrantedAuthority> authorities,
            String claimName
    ) {
        Object claim = jwt.getClaims().get(claimName);

        if (claim instanceof Collection<?> roles) {
            roles.forEach(role -> agregarRol(authorities, role.toString()));
        }

        if (claim instanceof String roleString) {
            List.of(roleString.split("[, ]+"))
                    .forEach(role -> agregarRol(authorities, role));
        }
    }

    private void agregarRol(Set<GrantedAuthority> authorities, String role) {
        if (role == null || role.isBlank()) {
            return;
        }

        String roleLimpio = role.trim();

        if (!roleLimpio.startsWith("ROLE_")) {
            roleLimpio = "ROLE_" + roleLimpio;
        }

        authorities.add(new SimpleGrantedAuthority(roleLimpio));
    } 
}