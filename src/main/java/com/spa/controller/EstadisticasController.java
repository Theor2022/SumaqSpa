package com.spa.controller;

import com.spa.dtos.EstadisticasDTO;
import com.spa.services.EstadisticasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controlador para estadísticas y reportes (solo ADMIN)
 */
@RestController
@RequestMapping("/api/estadisticas")
@PreAuthorize("hasRole('ADMIN')")
public class EstadisticasController {

    @Autowired
    private EstadisticasService estadisticasService;

    /**
     * Obtener estadísticas generales del spa
     */
    @GetMapping("/general")
    public ResponseEntity<EstadisticasDTO> obtenerEstadisticasGenerales() {
        EstadisticasDTO stats = estadisticasService.obtenerEstadisticasGenerales();
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtener estadísticas de un mes específico
     */
    @GetMapping("/mensual")
    public ResponseEntity<EstadisticasDTO> obtenerEstadisticasMensuales(
            @RequestParam int anio,
            @RequestParam int mes) {

        EstadisticasDTO stats = estadisticasService.obtenerEstadisticasMensuales(anio, mes);
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtener días más reservados del mes
     */
    @GetMapping("/dias-populares")
    public ResponseEntity<List<LocalDate>> obtenerDiasPopulares(
            @RequestParam int anio,
            @RequestParam int mes) {

        List<LocalDate> dias = estadisticasService.obtenerDiasMasReservados(anio, mes);
        return ResponseEntity.ok(dias);
    }

    /**
     * Obtener tratamientos más populares
     */
    @GetMapping("/tratamientos-populares")
    public ResponseEntity<Map<String, Long>> obtenerTratamientosPopulares() {
        Map<String, Long> populares = estadisticasService.obtenerTratamientosPopulares();
        return ResponseEntity.ok(populares);
    }
}