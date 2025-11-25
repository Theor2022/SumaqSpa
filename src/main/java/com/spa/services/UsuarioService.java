package com.spa.services;

import com.spa.dtos.AuthRequest;
import com.spa.dtos.RegisterRequest;
import com.spa.model.Usuario;
import com.spa.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Autenticar usuario y devolver token JWT
     */
    public String login(AuthRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(request.getUsername());

        if (!usuarioOpt.isPresent()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar que el usuario esté activo
        if (!usuario.getActive()) {
            throw new RuntimeException("Usuario inactivo");
        }

        // Comparar contraseña encriptada
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Generar token JWT
        return jwtService.generateToken(usuario.getUsername(), usuario.getRole());
    }

    /**
     * Registrar nuevo usuario con datos completos
     */
    public Usuario registrarUsuario(RegisterRequest request) {
        // Validar que el username no exista
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(request.getNombre());
        nuevoUsuario.setUsername(request.getUsername());
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        nuevoUsuario.setEmail(request.getEmail());
        nuevoUsuario.setTelefono(request.getTelefono());
        nuevoUsuario.setRole("USER"); // Por defecto es usuario normal
        nuevoUsuario.setActive(true);

        return usuarioRepository.save(nuevoUsuario);
    }

    /**
     * Registrar nuevo usuario (versión con AuthRequest - para compatibilidad)
     */
    public Usuario registrar(AuthRequest request) {
        // Validar que el username no exista
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        // Validar fortaleza de contraseña
        validarPassword(request.getPassword());

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(request.getUsername());
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        nuevoUsuario.setRole("USER"); // Por defecto USER
        nuevoUsuario.setActive(true);

        return usuarioRepository.save(nuevoUsuario);
    }

    /**
     * Validar fortaleza de contraseña
     */
    private void validarPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new RuntimeException("La contraseña debe contener al menos una mayúscula");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new RuntimeException("La contraseña debe contener al menos una minúscula");
        }

        if (!password.matches(".*[0-9].*")) {
            throw new RuntimeException("La contraseña debe contener al menos un número");
        }

        if (!password.matches(".*[!@#$%^&*].*")) {
            throw new RuntimeException("La contraseña debe contener al menos un carácter especial (!@#$%^&*)");
        }
    }

    /**
     * Obtener usuario por username
     */
    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}