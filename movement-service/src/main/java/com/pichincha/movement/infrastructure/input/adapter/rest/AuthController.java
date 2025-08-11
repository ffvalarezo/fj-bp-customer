package com.pichincha.movement.infrastructure.input.adapter.rest;

import com.pichincha.movement.domain.LoginResponse;
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
import java.util.HashMap;

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
    public Mono<LoginResponse> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        if (username.equals(this.username) && password.equals(this.password)) {
            String token = Jwts.builder().setSubject(username).claim("roles", "ROLE_ADMIN").setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                    .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256).compact();

            String refreshToken = Jwts.builder().setSubject(username).setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 7200000))
                    .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256).compact();

            Map<String, Object> user = new HashMap<>();
            user.put("username", username);
            user.put("roles", "ROLE_ADMIN");

            LoginResponse response = new LoginResponse(token, refreshToken, 3600, user);
            return Mono.just(response);
        }
        return Mono.error(new RuntimeException("Invalid credentials"));
    }

    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<LoginResponse> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();
            String username = claims.getSubject();
            if (!username.equals(this.username)) {
                return Mono.error(new RuntimeException("Invalid refresh token"));
            }
            String newToken = Jwts.builder()
                    .setSubject(username)
                    .claim("roles", "ROLE_ADMIN")
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                    .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                    .compact();
            String newRefreshToken = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 7200000))
                    .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                    .compact();
            Map<String, Object> user = new HashMap<>();
            user.put("username", username);
            user.put("roles", "ROLE_ADMIN");
            LoginResponse response = new LoginResponse(newToken, newRefreshToken, 3600, user);
            return Mono.just(response);
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Invalid refresh token"));
        }
    }

}