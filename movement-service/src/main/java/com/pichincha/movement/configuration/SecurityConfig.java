package com.pichincha.movement.configuration;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

	@Value("${jwt.secret:mySecretKey}")
	private String jwtSecret;

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		http.csrf(ServerHttpSecurity.CsrfSpec::disable)
				.authorizeExchange(
						exchange -> exchange.pathMatchers("/api/auth/**").permitAll().pathMatchers("/webjars/**")
								.permitAll().pathMatchers("/actuator/**").permitAll().pathMatchers("/swagger-ui/**")
								.permitAll().pathMatchers("/v3/api-docs/**").permitAll().anyExchange().authenticated())
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtDecoder(jwtDecoder())));
		return http.build();
	}

	@Bean
	public ReactiveJwtDecoder jwtDecoder() {
		return token -> {
			if (token == null || token.isBlank()) {
				return Mono.error(new RuntimeException("JWT token is missing or empty"));
			}
			try {
				var claimsJws = Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
						.parseClaimsJws(token);
				Map<String, Object> claims = new HashMap<>(claimsJws.getBody());
				Jwt jwt = new Jwt(token, Instant.ofEpochMilli(claimsJws.getBody().getIssuedAt().getTime()),
						Instant.ofEpochMilli(claimsJws.getBody().getExpiration().getTime()), Map.of("alg", "HS256"),
						claims);
				return Mono.just(jwt);
			} catch (Exception e) {
				return Mono.error(new RuntimeException("Invalid JWT", e));
			}
		};
	}

}