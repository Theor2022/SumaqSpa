package com.spa.config;

import com.spa.model.Usuario;
import com.spa.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Crear usuario administrador si no existe
        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setEmail("admin@sumaqspa.com");
            admin.setTelefono("0000000000");
            admin.setActive(true);
            usuarioRepository.save(admin);
            System.out.println("✅ Usuario administrador creado: admin / admin123");
        }

        // Crear usuario normal de prueba si no existe
        if (usuarioRepository.findByUsername("user").isEmpty()) {
            Usuario user = new Usuario();
            user.setNombre("Usuario de Prueba");
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole("USER");
            user.setEmail("user@example.com");
            user.setTelefono("1111111111");
            user.setActive(true);
            usuarioRepository.save(user);
            System.out.println("✅ Usuario de prueba creado: user / user123");
        }
    }
}