package com.spa.controller;
import com.spa.config.JwtUtil;
import com.spa.dtos.AuthRequest;
import com.spa.dtos.AuthResponse;
import com.spa.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthController(UsuarioService usuarioService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {

        try {
            usuarioService.register(request);
            return ResponseEntity.ok(Map.of("message", "Usuario registrado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error","Usuario ya existente"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            String token = jwtUtil.generateToken(request.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));

        } catch (BadCredentialsException e) {
            // Usuario o contraseña incorrectos
            return ResponseEntity
                    .status(401)
                    .body(Map.of("error", "Usuario o contraseña incorrectos"));
        } catch (Exception e) {
            // Error inesperado
            return ResponseEntity
                    .status(500)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
}
