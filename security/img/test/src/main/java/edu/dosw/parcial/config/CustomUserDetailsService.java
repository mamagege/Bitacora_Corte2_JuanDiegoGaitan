package edu.dosw.parcial.config;

import edu.dosw.parcial.persistence.entities.UserDocument;
import edu.dosw.parcial.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Servicio para carga de usuarios y autoridades desde MongoDB para Spring Security.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDocument doc = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));

        String role = doc.getRole() != null ? doc.getRole() : "ROLE_USER";
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        return new User(
                doc.getUsername(),
                doc.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}
