package edu.dosw.parcial.core.services;

import edu.dosw.parcial.config.JwtService;
import edu.dosw.parcial.core.models.UserAlreadyExistsException;
import edu.dosw.parcial.core.models.Usuario;
import edu.dosw.parcial.persistence.entities.UserDocument;
import edu.dosw.parcial.persistence.mappers.UserEntityMapper;
import edu.dosw.parcial.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio de autenticación y registro de usuarios en la plataforma.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserEntityMapper userEntityMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public Usuario register(Usuario usuario) {
        if (userRepository.existsByUsername(usuario.getUsername())) {
            throw new UserAlreadyExistsException("El nombre de usuario '" + usuario.getUsername() + "' ya está registrado");
        }
        if (usuario.getEmail() != null && userRepository.existsByEmail(usuario.getEmail())) {
            throw new UserAlreadyExistsException("El correo '" + usuario.getEmail() + "' ya está registrado");
        }

        String role = usuario.getRole();
        if (role == null || role.isBlank()) {
            role = "ROLE_USER";
        } else if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role.toUpperCase();
        }

        usuario.setRole(role);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setCreatedAt(LocalDateTime.now());

        UserDocument doc = userEntityMapper.toDocument(usuario);
        UserDocument saved = userRepository.save(doc);

        log.info("Usuario registrado exitosamente: {}", saved.getUsername());
        return userEntityMapper.toDomain(saved);
    }

    public String login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        log.info("Autenticación exitosa para usuario: {}", username);
        return token;
    }
}
