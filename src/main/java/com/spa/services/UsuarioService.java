package com.spa.services;

import com.spa.dtos.AuthRequest;
import com.spa.model.Usuario;
import com.spa.repository.UsuarioRepository;


import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        
    }
    public boolean login(AuthRequest request) {
    Optional<Usuario> usuario = usuarioRepository.findByUsername(request.getUsername());
    if (!usuario.isPresent()) {
        return false;
    }
    Usuario user = usuario.get();
    // Aquí deberías usar un encoder para comparar contraseñas en producción
    if (user.getPassword().equals(request.getPassword())) {
        return true;
    }
    return false;
}
   
}
