package com.spa.controller;

import com.spa.dtos.AuthRequest;
import com.spa.dtos.AuthResponse;
import com.spa.dtos.RegisterRequest;
import com.spa.model.Usuario;
import com.spa.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Login - devuelve JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            String token = usuarioService.login(request);
            Usuario usuario = usuarioService.findByUsername(request.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", usuario.getUsername());
            response.put("nombre", usuario.getNombre());
            response.put("email", usuario.getEmail());
            response.put("telefono", usuario.getTelefono());
            response.put("role", usuario.getRole());
            response.put("message", "Login exitoso");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Registro de nuevos usuarios con datos completos
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            Usuario nuevoUsuario = usuarioService.registrarUsuario(request);

            // Generar token para login automático
            String token = usuarioService.login(new AuthRequest(nuevoUsuario.getUsername(), request.getPassword()));

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", nuevoUsuario.getUsername());
            response.put("nombre", nuevoUsuario.getNombre());
            response.put("email", nuevoUsuario.getEmail());
            response.put("role", nuevoUsuario.getRole());
            response.put("message", "Usuario registrado exitosamente");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Registro de usuarios (versión simple - para compatibilidad)
     */
    @PostMapping("/register-simple")
    public ResponseEntity<?> registerSimple(@RequestBody AuthRequest request) {
        try {
            Usuario nuevoUsuario = usuarioService.registrar(request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario registrado exitosamente");
            response.put("username", nuevoUsuario.getUsername());
            response.put("role", nuevoUsuario.getRole());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}