package com.pe.laboratorio.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuesta con información completa de la orden
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenResponseDTO {

    private Long id;
    private String numeroOrden;
    private LocalDateTime fechaOrden;
    private String diagnostico;
    private String estado;
    private String prioridad;
    private String tipoAtencion;
    private String tipoMuestra;
    private String observaciones;
    private BigDecimal total;
    private LocalDateTime fechaTomaMuestra;
    private LocalDateTime fechaProcesamiento;
    private LocalDateTime fechaValidacion;
    private LocalDate fechaEntrega;

    // Información del paciente
    private Long patientId;
    private String patientFirstName;
    private String patientLastName;
    private String patientDni;

    // Información del médico
    private Long medicoId;
    private String medicoNombre;
    private String medicoFullName;

    // Información del usuario que registró
    private Long userId;
    private String userName;

    // Validación
    private Long validadoPorId;
    private String validadoPorName;

    // Detalles de la orden
    private List<OrdenDetalleDTO> detalles;

    // Auditoría
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdByName;

    /**
     * DTO anidado para los detalles de la orden
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrdenDetalleDTO {
        private Long id;
        private Long examId;
        private String examName;
        private Long equipoId;
        private String estado;
        private String resultado;
        private String valorReferencia;
        private String unidad;
        private String observaciones;
        private BigDecimal precio;
        private Boolean valorCritico;
        private Boolean fueraRango;
        private Boolean validadoPrimario;
        private Boolean validadoFinal;
        private String tecnologoName;
        private String biologoName;
        private String procesadoPorName;
        private String validadoPorName;
        private LocalDateTime fechaProcesamiento;
        private LocalDateTime fechaValidacionPrimaria;
        private LocalDateTime fechaValidacionFinal;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
