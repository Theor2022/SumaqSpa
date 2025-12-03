package com.spa.controller;

import com.spa.model.Tratamiento;
import com.spa.services.TratamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de tratamientos
 */
@RestController
@RequestMapping("/api/tratamientos")
public class TratamientoController {

    @Autowired
    private TratamientoService tratamientoService;

    /**
     * Obtener todos los tratamientos activos (público)
     */
    @GetMapping
    public ResponseEntity<List<Tratamiento>> obtenerTratamientosActivos() {
        List<Tratamiento> tratamientos = tratamientoService.findAllActivos();
        return ResponseEntity.ok(tratamientos);
    }

    /**
     * Obtener todos los tratamientos incluyendo inactivos (solo ADMIN)
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Tratamiento>> obtenerTodos() {
        List<Tratamiento> tratamientos = tratamientoService.findAll();
        return ResponseEntity.ok(tratamientos);
    }

    /**
     * Obtener tratamiento por ID (público)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tratamiento> obtenerPorId(@PathVariable Long id) {
        try {
            Tratamiento tratamiento = tratamientoService.findById(id);
            return ResponseEntity.ok(tratamiento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crear nuevo tratamiento (solo ADMIN)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> crearTratamiento(@RequestBody Tratamiento tratamiento) {
        try {
            Tratamiento nuevo = tratamientoService.save(tratamiento);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tratamiento creado exitosamente");
            response.put("tratamiento", nuevo);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Actualizar tratamiento (solo ADMIN)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarTratamiento(
            @PathVariable Long id,
            @RequestBody Tratamiento tratamiento) {

        try {
            tratamiento.setId(id);
            Tratamiento actualizado = tratamientoService.save(tratamiento);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tratamiento actualizado exitosamente");
            response.put("tratamiento", actualizado);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Desactivar tratamiento (solo ADMIN)
     */
    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> desactivarTratamiento(@PathVariable Long id) {
        try {
            tratamientoService.desactivar(id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Tratamiento desactivado exitosamente");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Activar tratamiento (solo ADMIN)
     */
    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activarTratamiento(@PathVariable Long id) {
        try {
            tratamientoService.activar(id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Tratamiento activado exitosamente");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Eliminar tratamiento permanentemente (solo ADMIN)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarTratamiento(@PathVariable Long id) {
        try {
            tratamientoService.deleteById(id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Tratamiento eliminado exitosamente");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Buscar por categoría (público)
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Tratamiento>> buscarPorCategoria(@PathVariable String categoria) {
        List<Tratamiento> tratamientos = tratamientoService.findByCategoria(categoria);
        return ResponseEntity.ok(tratamientos);
    }

    /**
     * Buscar por nombre (público)
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Tratamiento>> buscarPorNombre(@RequestParam String nombre) {
        List<Tratamiento> tratamientos = tratamientoService.buscarPorNombre(nombre);
        return ResponseEntity.ok(tratamientos);
    }

    /**
     * Obtener tratamientos más populares (público)
     */
    @GetMapping("/populares")
    public ResponseEntity<List<Tratamiento>> obtenerPopulares() {
        List<Tratamiento> tratamientos = tratamientoService.findMasReservados();
        return ResponseEntity.ok(tratamientos);
    }

    /**
     * Contar tratamientos activos (público)
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> contarActivos() {
        long count = tratamientoService.contarActivos();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}