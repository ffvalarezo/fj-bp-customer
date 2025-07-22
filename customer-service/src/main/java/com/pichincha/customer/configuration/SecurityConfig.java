package com.pichincha.customer.configuration;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

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
			Map<String, Object> claims = new HashMap<>();
			claims.put("sub", "frontend-user");
			claims.put("scope", "read write");
			claims.put("roles", List.of("ROLE_USER", "ROLE_ADMIN"));

			Jwt jwt = new Jwt(token, Instant.now(), Instant.now().plus(Duration.ofHours(1)), Map.of("alg", "none"),
					claims);

			return Mono.just(jwt);
		};
	}

}