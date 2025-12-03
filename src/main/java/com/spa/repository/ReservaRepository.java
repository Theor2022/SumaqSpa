package com.spa.repository;

import com.spa.model.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Repositorio para operaciones de base de datos con Reservas
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    /**
     * Obtener horas ocupadas en una fecha específica
     */
    @Query("SELECT r.hora FROM Reserva r WHERE r.fecha = :fecha AND r.estado = 'ACTIVA'")
    List<LocalTime> findHorasByFecha(@Param("fecha") LocalDate fecha);

    long countByEstado(String estado);

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
    @Query("SELECT r FROM Reserva r WHERE r.fecha BETWEEN :inicio AND :fin ORDER BY r.fecha, r.hora")
    List<Reserva> findByFechaRange(
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );

    /**
     * Contar reservas activas en una fecha
     */
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.estado = 'ACTIVA' AND r.fecha = :fecha")
    Long countReservasActivasPorFecha(@Param("fecha") LocalDate fecha);

    /**
     * Estadísticas por tratamiento
     */
    @Query("SELECT r.tratamiento, COUNT(r) FROM Reserva r WHERE r.estado = 'ACTIVA' GROUP BY r.tratamiento")
    List<Object[]> estadisticasPorTratamiento();

    /**
     * Obtener total de ingresos
     */
    @Query("SELECT SUM(r.total) FROM Reserva r WHERE r.estado = 'ACTIVA'")
    Double calcularIngresoTotal();

    /**
     * Ingresos en un mes específico
     */
    @Query("SELECT SUM(r.total) FROM Reserva r WHERE r.estado = 'ACTIVA' AND YEAR(r.fecha) = :anio AND MONTH(r.fecha) = :mes")
    Double calcularIngresoMensual(@Param("anio") int anio, @Param("mes") int mes);

    /**
     * Buscar reservas por correo
     */
    List<Reserva> findByCorreoOrderByFechaDesc(String correo);

    /**
     * Buscar reservas futuras de un cliente
     */
    @Query("SELECT r FROM Reserva r WHERE r.correo = :correo AND r.fecha >= :fechaActual AND r.estado = 'ACTIVA' ORDER BY r.fecha, r.hora")
    List<Reserva> findReservasFuturas(@Param("correo") String correo, @Param("fechaActual") LocalDate fechaActual);

    /**
     * Verificar si existe conflicto de horario
     */
    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.fecha = :fecha AND r.hora = :hora AND r.estado = 'ACTIVA' AND (:reservaId IS NULL OR r.id != :reservaId)")
    boolean existeConflictoHorario(
            @Param("fecha") LocalDate fecha,
            @Param("hora") LocalTime hora,
            @Param("reservaId") Long reservaId
    );
}