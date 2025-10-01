package com.spa.controller;

import com.spa.dtos.AuthRequest;

import com.spa.model.Usuario;
import com.spa.repository.UsuarioRepository;
import com.spa.services.UsuarioService;
import org.springframework.http.ResponseEntity;


import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    
    private final UsuarioService usuarioService;
   
    public AuthController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        
    }


  @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody AuthRequest request) {
    try {
        boolean authenticated = usuarioService.login(request);
        if (authenticated) {
            return ResponseEntity.ok("Credenciales correctas");
        } else {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    } catch (Exception e) {
         e.printStackTrace(); // esto aparecerá en consola
        throw e; // deja que el controlador maneje el 500
    }
}

}
