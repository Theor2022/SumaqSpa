package com.spa.services;

import com.spa.model.Reserva;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IReservaService {
    List<Reserva> findAll();

    Reserva findById(Long id);

    Reserva save(Reserva reserva);

    void deleteById(Long id);

    List<LocalTime> findHorasByFecha(LocalDate fecha);

    /**
     * Verificar si un horario específico está ocupado
     */
    boolean isHorarioOcupado(LocalDate fecha, LocalTime hora);
}