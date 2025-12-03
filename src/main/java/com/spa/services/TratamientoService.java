package com.spa.services;

import com.spa.model.Tratamiento;
import com.spa.repository.TratamientoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestión de tratamientos del spa
 */
@Service
@Transactional
public class TratamientoService {

    private static final Logger logger = LoggerFactory.getLogger(TratamientoService.class);

    @Autowired
    private TratamientoRepository tratamientoRepository;

    /**
     * Obtener todos los tratamientos activos (con cache)
     */
    @Cacheable(value = "tratamientos", key = "'activos'")
    public List<Tratamiento> findAllActivos() {
        logger.debug("Obteniendo tratamientos activos desde BD");
        return tratamientoRepository.findByActivoTrue();
    }

    /**
     * Obtener todos los tratamientos (incluyendo inactivos)
     */
    public List<Tratamiento> findAll() {
        return tratamientoRepository.findAll();
    }

    /**
     * Buscar tratamiento por ID
     */
    public Tratamiento findById(Long id) {
        return tratamientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tratamiento no encontrado con ID: " + id));
    }

    /**
     * Crear o actualizar tratamiento
     */
    @CacheEvict(value = "tratamientos", allEntries = true)
    public Tratamiento save(Tratamiento tratamiento) {
        if (tratamiento.getNombre() == null || tratamiento.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del tratamiento es obligatorio");
        }

        if (tratamiento.getPrecio() == null || tratamiento.getPrecio().doubleValue() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }

        if (tratamiento.getDuracionMinutos() == null || tratamiento.getDuracionMinutos() <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor a 0");
        }

        logger.info("Guardando tratamiento: {}", tratamiento.getNombre());
        return tratamientoRepository.save(tratamiento);
    }

    /**
     * Desactivar tratamiento (soft delete)
     */
    @CacheEvict(value = "tratamientos", allEntries = true)
    public void desactivar(Long id) {
        Tratamiento tratamiento = findById(id);
        tratamiento.setActivo(false);
        tratamientoRepository.save(tratamiento);
        logger.info("Tratamiento desactivado: {}", id);
    }

    /**
     * Activar tratamiento
     */
    @CacheEvict(value = "tratamientos", allEntries = true)
    public void activar(Long id) {
        Tratamiento tratamiento = findById(id);
        tratamiento.setActivo(true);
        tratamientoRepository.save(tratamiento);
        logger.info("Tratamiento activado: {}", id);
    }

    /**
     * Eliminar tratamiento permanentemente
     */
    @CacheEvict(value = "tratamientos", allEntries = true)
    public void deleteById(Long id) {
        if (!tratamientoRepository.existsById(id)) {
            throw new IllegalArgumentException("Tratamiento no encontrado con ID: " + id);
        }
        tratamientoRepository.deleteById(id);
        logger.info("Tratamiento eliminado permanentemente: {}", id);
    }

    /**
     * Buscar por categoría
     */
    @Cacheable(value = "tratamientos", key = "'categoria_' + #categoria")
    public List<Tratamiento> findByCategoria(String categoria) {
        return tratamientoRepository.findByCategoriaAndActivoTrue(categoria);
    }

    /**
     * Buscar por nombre
     */
    public List<Tratamiento> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return List.of();
        }
        return tratamientoRepository.buscarPorNombre(nombre.trim());
    }

    /**
     * Obtener tratamientos más reservados
     */
    @Cacheable(value = "tratamientos", key = "'populares'")
    public List<Tratamiento> findMasReservados() {
        return tratamientoRepository.findMasReservados();
    }

    /**
     * Contar tratamientos activos
     */
    public long contarActivos() {
        return tratamientoRepository.countByActivoTrue();
    }
}