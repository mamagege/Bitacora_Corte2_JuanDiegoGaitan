package com.dows.bitacora2.restaurante.service;

import com.dows.bitacora2.restaurante.exception.BloqueoTemporalException;
import com.dows.bitacora2.restaurante.model.dto.auth.LoginRequestDTO;
import com.dows.bitacora2.restaurante.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;



@Service
@lombok.extern.slf4j.Slf4j
@lombok.RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    
    
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    
    // NOTA: Para cumplir 100% con Stateless real y escalabilidad horizontal, 
    // este estado debería migrar a una BD o Redis. Se encapsula aquí para 
    // respetar MVC y Single Responsibility Principle.
    private final ConcurrentHashMap<String, Integer> intentosFallidos = new ConcurrentHashMap<>();

    @Override
    public String autenticarYGenerarToken(LoginRequestDTO dto) {
        String email = dto.email();
        if (intentosFallidos.getOrDefault(email, 0) >= 5) {
            log.warn("Bloqueo temporal por intentos fallidos para el usuario: {}", email);
            throw new BloqueoTemporalException("Usuario temporalmente bloqueado por demasiados intentos fallidos");
        }

        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, dto.password()));
            UserDetails user = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(user.getUsername(),
                    user.getAuthorities().iterator().next().getAuthority());
            
            // Éxito: limpiar intentos
            intentosFallidos.remove(email);
            log.info("Login exitoso para el usuario: {}", email);
            
            return token;
        } catch (AuthenticationException e) {
            intentosFallidos.put(email, intentosFallidos.getOrDefault(email, 0) + 1);
            log.warn("Intento de login fallido ({}) para el usuario: {}", intentosFallidos.get(email), email);
            throw e; // Deja que Spring maneje el error y devuelva 401
        }
    }
}
