package com.spa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una reserva en el sistema
 * Incluye auditoría y validaciones de negocio
 */
@Entity
@Table(name = "reservas", indexes = {
        @Index(name = "idx_fecha_hora", columnList = "fecha, hora"),
        @Index(name = "idx_estado", columnList = "estado")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String correo;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "ACTIVA";

    @Column(length = 500)
    private String motivoCancelacion;


    @Column(columnDefinition = "TEXT")
    private String notas;

    // Relación ManyToMany con Tratamientos
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "reserva_tratamientos",
            joinColumns = @JoinColumn(name = "reserva_id"),
            inverseJoinColumns = @JoinColumn(name = "tratamiento_id")
    )
    private List<Tratamiento> tratamientos = new ArrayList<>();

    // Campo legacy para compatibilidad (deprecado)
    @Column(length = 200)
    @Deprecated
    private String tratamiento;

    // Auditoría
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    /**
     * Validaciones antes de persistir o actualizar
     */
    @PrePersist
    @PreUpdate
    private void validarReserva() {
        // Validar fecha no sea pasada
        if (fecha.isBefore(LocalDate.now())) {
            throw new IllegalStateException("No se permiten reservas en fechas pasadas");
        }

        // Validar días de atención (Jueves a Lunes)
        DayOfWeek dia = fecha.getDayOfWeek();
        if (dia == DayOfWeek.TUESDAY || dia == DayOfWeek.WEDNESDAY) {
            throw new IllegalStateException("El spa no atiende martes y miércoles");
        }

        // Validar horario de atención (9:00 - 21:00)
        if (hora.isBefore(LocalTime.of(9, 0)) || hora.isAfter(LocalTime.of(21, 0))) {
            throw new IllegalStateException("El horario debe estar entre las 9:00 y 21:00 horas");
        }

        // Normalizar estado
        if (estado == null || estado.trim().isEmpty()) {
            estado = "ACTIVA";
        }
        estado = estado.toUpperCase();
    }

    /**
     * Verifica si la reserva puede ser cancelada (48 horas de anticipación)
     */
    public boolean puedeSerCancelada() {
        if (!"ACTIVA".equals(estado)) {
            return false;
        }
        LocalDateTime fechaHoraReserva = LocalDateTime.of(fecha, hora);
        LocalDateTime ahora = LocalDateTime.now();
        return fechaHoraReserva.minusHours(48).isAfter(ahora);
    }

    /**
     * Obtiene los nombres de los tratamientos concatenados
     */
    public String getTratamientosNombres() {
        if (tratamientos == null || tratamientos.isEmpty()) {
            return tratamiento; // Fallback al campo legacy
        }
        return tratamientos.stream()
                .map(Tratamiento::getNombre)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }
}