package com.pe.laboratorio.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para crear una nueva orden de laboratorio
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCreateDTO {

    private Long patientId;
    private Long medicoId;
    private String diagnostico;
    private String prioridad; // NORMAL, URGENTE, EMERGENCIA
    private String tipoAtencion; // AMBULATORIO, EMERGENCIA, HOSPITALIZADO
    private String tipoMuestra; // Ej: "Sangre", "Orina", etc.
    private String observaciones;
    private LocalDate fechaEntrega;

    /**
     * Lista de IDs de exámenes a incluir en la orden
     */
    private List<Long> examenesIds;

    /**
     * DTO anidado para especificar precio de cada examen (opcional)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExamenDTO {
        private Long examId;
        private BigDecimal precio;
    }
}
