package com.spa.services;

import com.spa.model.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Interface para el servicio de reservas
 */
public interface IReservaService {

    /**
     * Obtener todas las reservas
     */
    List<Reserva> findAll();

    /**
     * Obtener todas las reservas con paginación
     */
    Page<Reserva> findAll(Pageable pageable);

    /**
     * Buscar reserva por ID
     */
    Reserva findById(Long id);

    /**
     * Guardar o actualizar reserva
     */
    Reserva save(Reserva reserva);

    /**
     * Eliminar reserva por ID
     */
    void deleteById(Long id);

    /**
     * Obtener horas ocupadas en una fecha
     */
    List<LocalTime> findHorasByFecha(LocalDate fecha);

    /**
     * Verificar si un horario está ocupado
     */
    boolean isHorarioOcupado(LocalDate fecha, LocalTime hora);

    /**
     * Verificar si un horario está ocupado (excluyendo una reserva específica)
     */
    boolean isHorarioOcupado(LocalDate fecha, LocalTime hora, Long reservaId);

    /**
     * Buscar reservas por estado
     */
    List<Reserva> findByEstado(String estado);

    /**
     * Buscar reservas por estado con paginación
     */
    Page<Reserva> findByEstado(String estado, Pageable pageable);

    /**
     * Buscar reservas en un rango de fechas
     */
    List<Reserva> findByFechaRange(LocalDate inicio, LocalDate fin);

    /**
     * Cancelar una reserva
     */
    void cancelarReserva(Reserva reserva, String motivo);

    /**
     * Buscar reservas de un cliente por correo
     */
    List<Reserva> findReservasPorCorreo(String correo);

    /**
     * Buscar reservas futuras de un cliente
     */
    List<Reserva> findReservasFuturas(String correo);

    /**
     * Contar reservas activas en una fecha
     */
    Long countReservasActivasPorFecha(LocalDate fecha);
}