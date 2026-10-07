package com.dows.bitacora2.restaurante.controller;

import com.dows.bitacora2.restaurante.model.dto.auth.LoginRequestDTO;
import com.dows.bitacora2.restaurante.model.dto.auth.TokenResponseDTO;
import com.dows.bitacora2.restaurante.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    private final java.util.concurrent.ConcurrentHashMap<String, Integer> intentosFallidos = new java.util.concurrent.ConcurrentHashMap<>();

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @jakarta.validation.Valid LoginRequestDTO dto) {
        String email = dto.email();
        if (intentosFallidos.getOrDefault(email, 0) >= 5) {
            org.slf4j.LoggerFactory.getLogger(AuthController.class).warn("Bloqueo temporal por intentos fallidos para el usuario: {}", email);
            return ResponseEntity.status(org.springframework.http.HttpStatus.TOO_MANY_REQUESTS).build();
        }

        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, dto.password()));
            UserDetails user = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(user.getUsername(),
                    user.getAuthorities().iterator().next().getAuthority());
            
            // Éxito: limpiar intentos
            intentosFallidos.remove(email);
            org.slf4j.LoggerFactory.getLogger(AuthController.class).info("Login exitoso para el usuario: {}", email);
            
            return ResponseEntity.ok(new TokenResponseDTO(token));
        } catch (org.springframework.security.core.AuthenticationException e) {
            intentosFallidos.put(email, intentosFallidos.getOrDefault(email, 0) + 1);
            org.slf4j.LoggerFactory.getLogger(AuthController.class).warn("Intento de login fallido ({}) para el usuario: {}", intentosFallidos.get(email), email);
            throw e; // Deja que Spring maneje el error y devuelva 401
        }
    }
}
