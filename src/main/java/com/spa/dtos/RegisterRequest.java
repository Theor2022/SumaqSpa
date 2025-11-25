package com.spa.dtos;

public class RegisterRequest {
    private String nombre;
    private String username;
    private String password;
    private String email;
    private String telefono;

    public RegisterRequest() {}

    public RegisterRequest(String nombre, String username, String password, String email, String telefono) {
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.email = email;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}

