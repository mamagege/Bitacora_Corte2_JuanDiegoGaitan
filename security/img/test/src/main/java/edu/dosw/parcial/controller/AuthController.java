package edu.dosw.parcial.controller;

import edu.dosw.parcial.controller.dtos.request.LoginRequest;
import edu.dosw.parcial.controller.dtos.request.RegisterRequest;
import edu.dosw.parcial.controller.dtos.response.AuthResponse;
import edu.dosw.parcial.controller.mappers.AuthDtoMapper;
import edu.dosw.parcial.core.models.Usuario;
import edu.dosw.parcial.core.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para registro de usuarios e inicio de sesión con generación de JWT.
 * Endpoints públicos.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints públicos para registro e inicio de sesión con JWT")
public class AuthController {

    private final AuthService authService;
    private final AuthDtoMapper authDtoMapper;

    @Value("${jwt.expiration-ms:3600000}")
    private long expirationMs;

    @Operation(summary = "Registrar un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos"),
            @ApiResponse(responseCode = "409", description = "Nombre de usuario o correo ya existente")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        Usuario domain = authDtoMapper.toDomain(request);
        Usuario registered = authService.register(domain);
        String token = authService.login(request.getUsername(), request.getPassword());

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .username(registered.getUsername())
                .role(registered.getRole())
                .expiresInMs(expirationMs)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Iniciar sesión y obtener token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa, token retornado"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .username(request.getUsername())
                .role("ROLE_USER")
                .expiresInMs(expirationMs)
                .build();

        return ResponseEntity.ok(response);
    }
}
