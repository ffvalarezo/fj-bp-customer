package com.pichincha.customer.infrastructure.input.adapter.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/ping")
    public Mono<String> ping() {
        return Mono.just("API está funcionando sin autenticación.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public Mono<String> adminOnlyAccess() {
        return Mono.just("Acceso concedido al ADMIN");
    }
}
