package com.spa.repository;


import com.spa.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    @Query("SELECT r.hora FROM Reserva r WHERE r.fecha = :fecha AND r.estado = 'ACTIVA'")
    List<LocalTime> findHorasByFecha(@Param("fecha") LocalDate fecha);

    List<Reserva> findByEstado(String estado);
}