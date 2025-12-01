package com.spa.controller;

import com.spa.model.Reserva;
import com.spa.repository.ReservaRepository;
import com.spa.services.IReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    private final ReservaRepository reservaRepository;

    @Autowired
    private IReservaService reservaService;

    ReservaController(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    /**
     * Crear nueva reserva con validación de disponibilidad
     */
    @PostMapping
    public ResponseEntity<?> guardarReserva(@RequestBody Reserva reserva, Authentication authentication) {
        try {
            // Validar que la fecha no sea en el pasado
            if (reserva.getFecha().isBefore(LocalDate.now())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No se pueden hacer reservas en fechas pasadas");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Validar que el horario no esté ocupado
            boolean horarioOcupado = reservaService.isHorarioOcupado(reserva.getFecha(), reserva.getHora());
            if (horarioOcupado) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El horario seleccionado ya está reservado. Por favor, seleccione otro horario.");
                error.put("fecha", reserva.getFecha().toString());
                error.put("hora", reserva.getHora().toString());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }

            // Guardar la reserva
            Reserva reservaGuardada = reservaService.save(reserva);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Reserva guardada exitosamente");
            response.put("reserva", reservaGuardada);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al guardar la reserva: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Obtener todas las reservas (solo ADMIN)
     */
    @GetMapping("/reservas")
    public ResponseEntity<List<Reserva>> obtenerReservas() {
        List<Reserva> reservas = reservaRepository.findByEstado("ACTIVA");
        return ResponseEntity.ok(reservas);
    }

    /**
     * Obtener horarios ocupados para una fecha específica (público)
     */
    @GetMapping("/horarios")
    public ResponseEntity<List<String>> obtenerHorariosOcupados(@RequestParam String fecha) {
        try {
            LocalDate fechaConsulta = LocalDate.parse(fecha);
            List<LocalTime> horasOcupadas = reservaService.findHorasByFecha(fechaConsulta);

            // Convertir LocalTime a String "HH:mm"
            List<String> horasComoTexto = horasOcupadas.stream()
                    .map(h -> h.toString().substring(0, 5)) // formato HH:mm
                    .collect(Collectors.toList());

            return ResponseEntity.ok(horasComoTexto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Eliminar reserva (solo ADMIN)
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReserva(@PathVariable Long id) {
        try {
            return reservaRepository.findById(id)
                    .map(reserva -> {
                        reserva.setEstado("CANCELADA");
                        reservaRepository.save(reserva);
                        Map<String, String> response = new HashMap<>();
                        response.put("message", "Reserva cancelada exitosamente");
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        Map<String, String> error = new HashMap<>();
                        error.put("error", "Reserva no encontrada");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al cancelar la reserva: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Actualizar reserva (solo ADMIN)
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReserva(@PathVariable Long id, @RequestBody Reserva reservaActualizada) {
        try {
            Reserva reservaExistente = reservaService.findById(id);

            if (reservaExistente == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Reserva no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            // Validar que la nueva fecha no sea en el pasado
            if (reservaActualizada.getFecha().isBefore(LocalDate.now())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No se pueden hacer reservas en fechas pasadas");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Si se cambia fecha u hora, validar que el nuevo horario esté disponible
            boolean cambioHorario = !reservaExistente.getFecha().equals(reservaActualizada.getFecha())
                    || !reservaExistente.getHora().equals(reservaActualizada.getHora());

            if (cambioHorario) {
                boolean horarioOcupado = reservaService.isHorarioOcupado(
                        reservaActualizada.getFecha(),
                        reservaActualizada.getHora()
                );

                if (horarioOcupado) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "El nuevo horario seleccionado ya está reservado");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
                }
            }

            // Actualizar campos
            reservaExistente.setNombre(reservaActualizada.getNombre());
            reservaExistente.setCorreo(reservaActualizada.getCorreo());
            reservaExistente.setTelefono(reservaActualizada.getTelefono());
            reservaExistente.setTratamiento(reservaActualizada.getTratamiento());
            reservaExistente.setTotal(reservaActualizada.getTotal());
            reservaExistente.setFecha(reservaActualizada.getFecha());
            reservaExistente.setHora(reservaActualizada.getHora());

            Reserva reservaGuardada = reservaService.save(reservaExistente);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Reserva actualizada exitosamente");
            response.put("reserva", reservaGuardada);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar la reserva: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}