package com.spa.controller;

import com.spa.model.Reserva;
import com.spa.repository.ReservaRepository;
import com.spa.services.IReservaService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaRepository reservaRepository;

    @Autowired
    private IReservaService reservaService;

    ReservaController(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @PostMapping
    public ResponseEntity<String> guardarReserva(@RequestBody Reserva reserva) {
        reservaService.save(reserva);
        return ResponseEntity.ok("Reserva guardada exitosamente");
    }

    @GetMapping("/reservas")
    public List<Reserva> obtenerReservas() {
        return reservaRepository.findAll();
    }
    

    @GetMapping("/horarios")
    public ResponseEntity<List<String>> obtenerHorariosOcupados(@RequestParam String fecha) {
        LocalDate fechaConsulta = LocalDate.parse(fecha);
        List<LocalTime> horasOcupadas = reservaService.findHorasByFecha(fechaConsulta);

        // Convertir LocalTime a String "HH:mm"
        List<String> horasComoTexto = horasOcupadas.stream()
                .map(h -> h.toString().substring(0, 5)) // formato HH:mm
                .collect(Collectors.toList());

        return ResponseEntity.ok(horasComoTexto);
    }

    @DeleteMapping("/delete/{id}")
public ResponseEntity<String> deleteReserva(@PathVariable Long id) {
    if (reservaRepository.existsById(id)) {
        reservaRepository.deleteById(id);
        return ResponseEntity.ok("Reserva eliminada exitosamente");
    } else {
        return ResponseEntity.status(404).body("Reserva no encontrada");
    }
}
}
