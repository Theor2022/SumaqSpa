package com.spa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un tratamiento disponible en el spa
 */
@Entity
@Table(name = "tratamientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tratamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Integer duracionMinutos;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(length = 500)
    private String imagenUrl;

    @Column(length = 50)
    private String categoria;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "tratamientos")
    @JsonIgnore
    private List<Reserva> reservas = new ArrayList<>();

    public Tratamiento(String nombre, String descripcion, Integer duracionMinutos, BigDecimal precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracionMinutos = duracionMinutos;
        this.precio = precio;
        this.activo = true;
    }

    public String getDuracionFormateada() {
        if (duracionMinutos < 60) {
            return duracionMinutos + " minutos";
        }
        int horas = duracionMinutos / 60;
        int minutos = duracionMinutos % 60;
        return minutos > 0 ? horas + "h " + minutos + "min" : horas + " hora" + (horas > 1 ? "s" : "");
    }
}