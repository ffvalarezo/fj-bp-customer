package com.pichincha.movement.infrastructure.input.adapter.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Value("${jwt.secret:mySecretKey}")
	private String jwtSecret;

	@Value("${jwt.username:username}")
	private String username;

	@Value("${jwt.password:password}")
	private String password;

	@GetMapping("/ping")
	public Mono<String> ping() {
		return Mono.just("API está funcionando sin autenticación.");
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/admin")
	public Mono<String> adminOnlyAccess() {
		return Mono.just("Acceso concedido al ADMIN");
	}

	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Mono<String> login(@RequestBody Map<String, String> credentials) {
		String username = credentials.get("username");
		String password = credentials.get("password");
		if (username.equals(this.username) && password.equals(this.password)) {
			String token = Jwts.builder().setSubject(username).claim("roles", "ROLE_ADMIN").setIssuedAt(new Date())
					.setExpiration(new Date(System.currentTimeMillis() + 3600000))
					.signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256).compact();
			return Mono.just(token);
		}
		return Mono.error(new RuntimeException("Invalid credentials"));
	}
}