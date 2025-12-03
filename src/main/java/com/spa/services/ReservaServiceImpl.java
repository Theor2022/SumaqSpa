package com.spa.services;

import com.spa.model.Reserva;
import com.spa.repository.ReservaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Implementación del servicio de reservas con cache y validaciones
 */
@Service
@Transactional
public class ReservaServiceImpl implements IReservaService {

    private static final Logger logger = LoggerFactory.getLogger(ReservaServiceImpl.class);

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    @Override
    public Page<Reserva> findAll(Pageable pageable) {
        return reservaRepository.findAll(pageable);
    }

    @Override
    public Reserva findById(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + id));
    }

    @Override
    @CacheEvict(value = "horariosDisponibles", key = "#reserva.fecha")
    public Reserva save(Reserva reserva) {
        logger.info("Guardando reserva para {} en fecha {}", reserva.getNombre(), reserva.getFecha());

        // Validar conflicto de horario
        if (isHorarioOcupado(reserva.getFecha(), reserva.getHora(), reserva.getId())) {
            throw new IllegalStateException("El horario seleccionado ya está ocupado");
        }

        try {
            Reserva saved = reservaRepository.save(reserva);
            logger.info("Reserva {} guardada exitosamente", saved.getId());

            // Enviar email de confirmación
            try {
                emailService.enviarConfirmacionReserva(saved);
            } catch (Exception e) {
                logger.error("Error al enviar email de confirmación: {}", e.getMessage());
                // No fallar la transacción por error de email
            }

            return saved;
        } catch (Exception e) {
            logger.error("Error al guardar reserva: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @CacheEvict(value = "horariosDisponibles", allEntries = true)
    public void deleteById(Long id) {
        if (!reservaRepository.existsById(id)) {
            throw new IllegalArgumentException("Reserva no encontrada con ID: " + id);
        }
        reservaRepository.deleteById(id);
        logger.info("Reserva eliminada: {}", id);
    }

    @Override
    @Cacheable(value = "horariosDisponibles", key = "#fecha")
    public List<LocalTime> findHorasByFecha(LocalDate fecha) {
        logger.debug("Obteniendo horarios ocupados para fecha: {}", fecha);
        return reservaRepository.findHorasByFecha(fecha);
    }

    @Override
    public boolean isHorarioOcupado(LocalDate fecha, LocalTime hora) {
        return isHorarioOcupado(fecha, hora, null);
    }

    @Override
    public boolean isHorarioOcupado(LocalDate fecha, LocalTime hora, Long reservaId) {
        return reservaRepository.existeConflictoHorario(fecha, hora, reservaId);
    }

    @Override
    public List<Reserva> findByEstado(String estado) {
        return reservaRepository.findByEstado(estado);
    }

    @Override
    public Page<Reserva> findByEstado(String estado, Pageable pageable) {
        return reservaRepository.findByEstado(estado, pageable);
    }

    @Override
    public List<Reserva> findByFechaRange(LocalDate inicio, LocalDate fin) {
        return reservaRepository.findByFechaRange(inicio, fin);
    }

    @Override
    @CacheEvict(value = "horariosDisponibles", key = "#reserva.fecha")
    public void cancelarReserva(Reserva reserva, String motivo) {
        if (!reserva.puedeSerCancelada()) {
            throw new IllegalStateException(
                    "No se puede cancelar una reserva con menos de 48 horas de anticipación"
            );
        }

        reserva.setEstado("CANCELADA");
        reserva.setMotivoCancelacion(motivo);
        reservaRepository.save(reserva);

        logger.info("Reserva {} cancelada. Motivo: {}", reserva.getId(), motivo);

        // Enviar email de cancelación
        try {
            emailService.enviarNotificacionCancelacion(reserva);
        } catch (Exception e) {
            logger.error("Error al enviar email de cancelación: {}", e.getMessage());
        }
    }

    @Override
    public List<Reserva> findReservasPorCorreo(String correo) {
        return reservaRepository.findByCorreoOrderByFechaDesc(correo);
    }

    @Override
    public List<Reserva> findReservasFuturas(String correo) {
        return reservaRepository.findReservasFuturas(correo, LocalDate.now());
    }

    @Override
    public Long countReservasActivasPorFecha(LocalDate fecha) {
        return reservaRepository.countReservasActivasPorFecha(fecha);
    }
}