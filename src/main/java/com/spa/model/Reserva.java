package com.spa.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String correo;
    private String telefono;
    private String tratamiento;
    private Double total;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "hora")
    private LocalTime hora;

    @Column(name = "estado")
    private String estado = "ACTIVA"; // Por defecto, la reserva está activa

    // Constructor vacío para JPA
    public Reserva() {}

    // Constructor completo
    public Reserva(Long id, String nombre, String correo, String telefono,
                   String tratamiento, Double total, LocalDate fecha, LocalTime hora, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.tratamiento = tratamiento;
        this.total = total;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
    }

    // ----- Getters y Setters -----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
