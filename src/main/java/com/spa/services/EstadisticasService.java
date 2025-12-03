package com.spa.services;

import com.spa.dtos.EstadisticasDTO;
import com.spa.repository.ReservaRepository;
import com.spa.repository.TratamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio para generar estadísticas del spa
 */
@Service
public class EstadisticasService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    /**
     * Obtener estadísticas generales (con cache de 5 minutos)
     */
    @Cacheable(value = "estadisticas", key = "'general'")
    public EstadisticasDTO obtenerEstadisticasGenerales() {
        EstadisticasDTO stats = new EstadisticasDTO();

        // Total de reservas
        stats.setTotalReservas(reservaRepository.count());

        // Reservas por estado
        stats.setReservasActivas(reservaRepository.countByEstado("ACTIVA"));
        stats.setReservasCanceladas(reservaRepository.countByEstado("CANCELADA"));

        // Ingresos
        Double ingresoTotal = reservaRepository.calcularIngresoTotal();
        stats.setIngresoTotal(ingresoTotal != null ? BigDecimal.valueOf(ingresoTotal) : BigDecimal.ZERO);

        // Ingreso del mes actual
        LocalDate now = LocalDate.now();
        Double ingresoMensual = reservaRepository.calcularIngresoMensual(now.getYear(), now.getMonthValue());
        stats.setIngresoMensual(ingresoMensual != null ? BigDecimal.valueOf(ingresoMensual) : BigDecimal.ZERO);

        // Reservas por tratamiento
        List<Object[]> reservasPorTratamiento = reservaRepository.estadisticasPorTratamiento();
        Map<String, Long> mapaTratamientos = new HashMap<>();
        for (Object[] obj : reservasPorTratamiento) {
            mapaTratamientos.put((String) obj[0], (Long) obj[1]);
        }
        stats.setReservasPorTratamiento(mapaTratamientos);

        // Calcular tasa de ocupación (ejemplo simplificado)
        stats.setTasaOcupacion(calcularTasaOcupacion());

        return stats;
    }

    /**
     * Obtener estadísticas de un mes específico
     */
    public EstadisticasDTO obtenerEstadisticasMensuales(int anio, int mes) {
        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin = inicio.plusMonths(1).minusDays(1);

        var reservas = reservaRepository.findByFechaRange(inicio, fin);

        EstadisticasDTO stats = new EstadisticasDTO();
        stats.setTotalReservas((long) reservas.size());
        stats.setReservasActivas(reservas.stream().filter(r -> "ACTIVA".equals(r.getEstado())).count());
        stats.setReservasCanceladas(reservas.stream().filter(r -> "CANCELADA".equals(r.getEstado())).count());

        double totalIngresos = reservas.stream()
                .filter(r -> "ACTIVA".equals(r.getEstado()))
                .map(r -> r.getTotal() != null ? r.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();

        stats.setIngresoMensual(BigDecimal.valueOf(totalIngresos));

        return stats;
    }

    /**
     * Obtener días más reservados del mes
     */
    public List<LocalDate> obtenerDiasMasReservados(int anio, int mes) {
        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin = inicio.plusMonths(1).minusDays(1);

        var reservas = reservaRepository.findByFechaRange(inicio, fin);

        return reservas.stream()
                .collect(Collectors.groupingBy(r -> r.getFecha(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<LocalDate, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Calcular tasa de ocupación aproximada
     * Considera horarios disponibles vs ocupados
     */
    private Double calcularTasaOcupacion() {
        LocalDate hoy = LocalDate.now();
        LocalDate finMes = hoy.plusMonths(1);

        long totalReservas = reservaRepository.findByFechaRange(hoy, finMes).size();

        // Días hábiles aproximados en el mes (asumiendo 5 días/semana, 10 horas/día, 2 turnos/hora)
        long diasHabiles = 20;
        long turnosPosibles = diasHabiles * 10 * 2; // ~400 turnos

        if (turnosPosibles == 0) return 0.0;

        return (totalReservas * 100.0) / turnosPosibles;
    }

    /**
     * Obtener tratamientos más populares
     */
    public Map<String, Long> obtenerTratamientosPopulares() {
        List<Object[]> resultados = reservaRepository.estadisticasPorTratamiento();

        return resultados.stream()
                .limit(5)
                .collect(Collectors.toMap(
                        obj -> (String) obj[0],
                        obj -> (Long) obj[1]
                ));
    }
}