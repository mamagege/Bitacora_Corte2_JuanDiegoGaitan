package edu.dosw.parcial.persistence.mappers;

import edu.dosw.parcial.core.models.Usuario;
import edu.dosw.parcial.persistence.entities.UserDocument;
import org.springframework.stereotype.Component;

/**
 * Mapper OUT: Transforma bidireccionalmente entre el modelo de Dominio (Usuario)
 * y el Documento de MongoDB (UserDocument).
 */
@Component
public class UserEntityMapper {

    public UserDocument toDocument(Usuario domain) {
        if (domain == null) {
            return null;
        }
        return UserDocument.builder()
                .id(domain.getId())
                .username(domain.getUsername())
                .password(domain.getPassword())
                .email(domain.getEmail())
                .role(domain.getRole())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public Usuario toDomain(UserDocument doc) {
        if (doc == null) {
            return null;
        }
        return Usuario.builder()
                .id(doc.getId())
                .username(doc.getUsername())
                .password(doc.getPassword())
                .email(doc.getEmail())
                .role(doc.getRole())
                .createdAt(doc.getCreatedAt())
                .build();
    }
}
