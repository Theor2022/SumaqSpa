package com.spa.services;

import com.spa.model.Reserva;
import com.spa.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservaServiceImpl implements IReservaService {

    @Autowired
    private ReservaRepository repo;
    @Autowired
    private ReservaRepository reservaRepository;


    @Override
    public List<Reserva> findAll() {

        return repo.findAll();

    }

    @Override
    public Reserva findById(Long id) {

        return repo.findById(id).orElse(null);
    }

    @Override
    public Reserva save(Reserva reserva) {

        return repo.save(reserva);
    }

    @Override
    public void deleteById(Long id) {

        repo.deleteById(id);

    }

    @Override
    public List<LocalTime> findHorasByFecha(LocalDate fecha) {
        return reservaRepository.findHorasByFecha(fecha);
    }
}
