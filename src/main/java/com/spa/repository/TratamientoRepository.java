package com.spa.repository;

import com.spa.model.Tratamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para operaciones de base de datos con Tratamientos
 */
@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {

    /**
     * Buscar tratamientos activos
     */
    List<Tratamiento> findByActivoTrue();

    /**
     * Buscar por categoría
     */
    List<Tratamiento> findByCategoriaAndActivoTrue(String categoria);

    /**
     * Buscar tratamientos por nombre (búsqueda parcial)
     */
    @Query("SELECT t FROM Tratamiento t WHERE LOWER(t.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND t.activo = true")
    List<Tratamiento> buscarPorNombre(String nombre);

    /**
     * Obtener tratamientos más reservados
     */
    @Query("SELECT t FROM Tratamiento t JOIN t.reservas r WHERE r.estado = 'ACTIVA' GROUP BY t ORDER BY COUNT(r) DESC")
    List<Tratamiento> findMasReservados();

    /**
     * Contar tratamientos activos
     */
    long countByActivoTrue();
}