package com.spa.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO para estadísticas del sistema
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasDTO {

    private Long totalReservas;
    private Long reservasActivas;
    private Long reservasCanceladas;
    private BigDecimal ingresoTotal;
    private BigDecimal ingresoMensual;
    private Map<String, Long> reservasPorTratamiento;
    private List<LocalDate> diasMasReservados;
    private Map<String, Integer> horariosPopulares;
    private Double tasaOcupacion; // Porcentaje de ocupación
}