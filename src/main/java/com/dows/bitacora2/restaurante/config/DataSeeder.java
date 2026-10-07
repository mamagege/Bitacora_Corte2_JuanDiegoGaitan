package com.dows.bitacora2.restaurante.config;

import com.dows.bitacora2.restaurante.persistence.entity.UsuarioEntity;
import com.dows.bitacora2.restaurante.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Verifica si la tabla de usuarios está vacía
        if (usuarioRepository.count() == 0) {
            crearUsuario("admin@restaurante.com", "admin123", "ADMIN");
            crearUsuario("mesero@restaurante.com", "mesero123", "MESERO");
            crearUsuario("cocina@restaurante.com", "cocina123", "COCINA");
            crearUsuario("cliente@restaurante.com", "cliente123", "CLIENTE");
            
            System.out.println("====== USUARIOS POR DEFECTO CREADOS ======");
            System.out.println("ADMIN:   admin@restaurante.com   / admin123");
            System.out.println("MESERO:  mesero@restaurante.com  / mesero123");
            System.out.println("COCINA:  cocina@restaurante.com  / cocina123");
            System.out.println("CLIENTE: cliente@restaurante.com / cliente123");
            System.out.println("==========================================");
        }
    }

    private void crearUsuario(String email, String password, String rol) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(rol);
        usuarioRepository.save(usuario);
    }
}
