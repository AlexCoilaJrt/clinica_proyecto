package com.pe.laboratorio.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para recibir filtros de búsqueda de órdenes.
 * Las fechas se reciben como LocalDate y se convertirán a LocalDateTime en el
 * servicio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenFilterDTO {

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Long patientId;
    private Long medicoId;
    private String estado; // PENDIENTE, EN_PROCESO, VALIDADO, ENTREGADO
    private String prioridad; // NORMAL, URGENTE, EMERGENCIA
}
