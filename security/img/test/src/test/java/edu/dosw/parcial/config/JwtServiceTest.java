package edu.dosw.parcial.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);

        userDetails = new User("admin", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("generateToken: debe generar un token JWT firmado válido y extraer su subject")
    void testGenerateAndValidateToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isBlank());

        String username = jwtService.extractUsername(token);
        assertEquals("admin", username);

        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertTrue(isValid);

        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    @DisplayName("isTokenValid: debe rechazar un token si el username no coincide")
    void testTokenInvalidForDifferentUser() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = new User("otherUser", "password", Collections.emptyList());

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }
}
