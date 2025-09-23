package com.spa.controller;

import com.spa.model.Reserva;
import com.spa.services.IReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private IReservaService reservaService;

    @PostMapping
    public ResponseEntity<String> guardarReserva(@RequestBody Reserva reserva) {
        reservaService.save(reserva);
        return ResponseEntity.ok("Reserva guardada exitosamente");
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
}
