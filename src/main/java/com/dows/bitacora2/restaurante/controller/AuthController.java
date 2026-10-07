package com.dows.bitacora2.restaurante.controller;

import com.dows.bitacora2.restaurante.model.dto.auth.LoginRequestDTO;
import com.dows.bitacora2.restaurante.model.dto.auth.TokenResponseDTO;
import com.dows.bitacora2.restaurante.service.IAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@lombok.RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @jakarta.validation.Valid LoginRequestDTO dto) {
        String token = authService.autenticarYGenerarToken(dto);
        return ResponseEntity.ok(new TokenResponseDTO(token));
    }
}
