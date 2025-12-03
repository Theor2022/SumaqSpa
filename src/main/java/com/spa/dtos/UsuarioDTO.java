package com.spa.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para transferir información de usuario sin exponer contraseña
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Long id;
    private String nombre;
    private String username;
    private String email;
    private String telefono;
    private String role;
    private Boolean active;
    private LocalDateTime createdAt;

    // Constructor útil
    public UsuarioDTO(Long id, String nombre, String username, String email, String role) {
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.email = email;
        this.role = role;
    }
}