package edu.dosw.parcial.controller.mappers;

import edu.dosw.parcial.controller.dtos.request.RegisterRequest;
import edu.dosw.parcial.core.models.Usuario;
import org.springframework.stereotype.Component;

/**
 * Mapper IN: Transforma entre DTOs de autenticación y modelo Usuario del Dominio.
 */
@Component
public class AuthDtoMapper {

    public Usuario toDomain(RegisterRequest request) {
        if (request == null) {
            return null;
        }
        return Usuario.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .role(request.getRole())
                .build();
    }
}
