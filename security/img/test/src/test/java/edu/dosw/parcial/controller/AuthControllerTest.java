package edu.dosw.parcial.controller;

import edu.dosw.parcial.controller.dtos.request.LoginRequest;
import edu.dosw.parcial.controller.dtos.request.RegisterRequest;
import edu.dosw.parcial.controller.dtos.response.AuthResponse;
import edu.dosw.parcial.controller.mappers.AuthDtoMapper;
import edu.dosw.parcial.core.models.Usuario;
import edu.dosw.parcial.core.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private AuthDtoMapper authDtoMapper;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authDtoMapper = new AuthDtoMapper();
        authController = new AuthController(authService, authDtoMapper);
        ReflectionTestUtils.setField(authController, "expirationMs", 3600000L);
    }

    @Test
    @DisplayName("register: debe registrar al usuario y retornar 201 Created con token")
    void testRegister() {
        RegisterRequest request = RegisterRequest.builder()
                .username("nuevoUsuario")
                .password("secret123")
                .email("nuevo@bobsbarber.com")
                .role("USER")
                .build();

        Usuario mockRegistered = Usuario.builder()
                .username("nuevoUsuario")
                .email("nuevo@bobsbarber.com")
                .role("ROLE_USER")
                .build();

        when(authService.register(any(Usuario.class))).thenReturn(mockRegistered);
        when(authService.login("nuevoUsuario", "secret123")).thenReturn("mock-jwt-token");

        ResponseEntity<AuthResponse> response = authController.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("mock-jwt-token", response.getBody().getToken());
        assertEquals("nuevoUsuario", response.getBody().getUsername());
    }

    @Test
    @DisplayName("login: debe autenticar credenciales válidas y retornar 200 OK con token")
    void testLogin() {
        LoginRequest request = LoginRequest.builder()
                .username("admin")
                .password("admin123")
                .build();

        when(authService.login("admin", "admin123")).thenReturn("admin-token-jwt");

        ResponseEntity<AuthResponse> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("admin-token-jwt", response.getBody().getToken());
    }
}
