package com.pichincha.account.configuration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Value("${jwt.secret:mySecretKey}")
    private String jwtSecret;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/api/auth/**", "/webjars/**", "/actuator/**",
                                "/swagger-ui/**", "/v3/api-docs/**")
                        .permitAll()
                        .anyExchange()
                        .authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtDecoder(jwtDecoder())))
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader("x-app");
        configuration.addExposedHeader("x-guid");
        configuration.addExposedHeader("x-channel");
        configuration.addExposedHeader("x-medium");
        configuration.addExposedHeader("x-session");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return token -> {
            if (token == null || token.isBlank()) {
                return Mono.error(new RuntimeException("JWT token is missing or empty"));
            }
            try {
                var claimsJws = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                        .build()
                        .parseClaimsJws(token);
                Map<String, Object> claims = new HashMap<>(claimsJws.getBody());
                Jwt jwt = new Jwt(
                        token,
                        Instant.ofEpochMilli(claimsJws.getBody().getIssuedAt().getTime()),
                        Instant.ofEpochMilli(claimsJws.getBody().getExpiration().getTime()),
                        Map.of("alg", "HS256"),
                        claims
                );
                return Mono.just(jwt);
            } catch (Exception e) {
                return Mono.error(new RuntimeException("Invalid JWT", e));
            }
        };
    }
}