package com.dows.bitacora2.restaurante.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dows.bitacora2.restaurante.repository.UsuarioRepository;
import com.dows.bitacora2.restaurante.persistence.entity.UsuarioEntity;
import com.dows.bitacora2.restaurante.security.JwtAuthFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UsuarioRepository usuarioRepository,
            JwtAuthFilter jwtAuthFilter, com.dows.bitacora2.restaurante.security.JwtUtil jwtUtil,
            org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; script-src 'self'"))
                        .frameOptions(frame -> frame.deny()) // bloquea iframes (Clickjacking)
                        .xssProtection(xss -> xss.headerValue( // activa filtro XSS del navegador
                                org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/platos/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/platos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/platos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/platos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/platos/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/**")
                        .hasAnyRole("MESERO", "COCINA", "ADMIN", "CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/pedidos").hasAnyRole("CLIENTE", "MESERO")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/pedidos/**").hasAnyRole("CLIENTE", "MESERO")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/pedidos/*/estado").hasAnyRole("COCINA", "MESERO")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/pedidos/**").hasAnyRole("MESERO", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/mesas/**").hasAnyRole("CLIENTE", "MESERO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/mesas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/mesas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/mesas/*/estado").hasAnyRole("MESERO", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/mesas/*/abrir-cuenta")
                        .hasAnyRole("CLIENTE", "MESERO")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/mesas/*/cerrar-cuenta").hasRole("MESERO")

                        .requestMatchers(HttpMethod.GET, "/api/v1/cuentas/**").hasAnyRole("CLIENTE", "MESERO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/cuentas").hasAnyRole("CLIENTE", "MESERO")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/cuentas/*/actualizar-total")
                        .hasAnyRole("CLIENTE", "MESERO")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/cuentas/*/pagar").hasRole("MESERO")

                        .anyRequest().authenticated())
                .httpBasic(basic -> basic.disable())
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler(usuarioRepository, jwtUtil)))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setStatus(401);
                            response.getWriter().write(
                                    "{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Debes iniciar sesion o proveer un token valido\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json");
                            response.setStatus(403);
                            response.getWriter().write(
                                    "{\"status\": 403, \"error\": \"Forbidden\", \"message\": \"No tienes permisos para realizar esta accion\"}");
                        }))
                // No se usa STATELESS porque rompería OAuth2
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private AuthenticationSuccessHandler oAuth2SuccessHandler(UsuarioRepository usuarioRepository,
            com.dows.bitacora2.restaurante.security.JwtUtil jwtUtil) {
        return (request, response, authentication) -> {
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
            String email = oauthUser.getAttribute("email");
            String nombre = oauthUser.getAttribute("name");

            // Crear o actualizar el usuario en la BD con email y nombre
            UsuarioEntity usuario = usuarioRepository.findByEmail(email).orElseGet(() -> {
                UsuarioEntity newUser = new UsuarioEntity();
                newUser.setEmail(email);
                newUser.setPassword("OAUTH2_USER"); // No usa contraseña
                newUser.setRol("CLIENTE"); // Rol por defecto
                log.info("Nuevo usuario registrado desde OAuth2: {}", email);
                return usuarioRepository.save(newUser);
            });

            log.info("Login OAuth2 exitoso para: {} ({})", nombre, email);

            String token = jwtUtil.generateToken(email, usuario.getRol());

            response.setContentType("application/json");
            response.getWriter()
                    .write("{\n  \"message\": \"Copia el token JWT de abajo y pegalo en Swagger\",\n  \"token\": \""
                            + token + "\"\n}");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
