package com.spa.services;

import com.spa.model.Reserva;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public interface IReservaService {
    List<Reserva> findAll();

    Reserva findById(Long Id);

    Reserva save(Reserva reserva);

    void deleteById(Long id);

    List<LocalTime> findHorasByFecha(LocalDate fecha);
}

