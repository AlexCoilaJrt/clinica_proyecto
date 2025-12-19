package com.pe.laboratorio.subexam.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.pe.laboratorio.exam.enums.TipoResultado;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubExamResponse {
    private Long id;
    private Long examenId;
    private String examenNombre;
    private String examenCodigo;
    private String codigo;
    private String nombre;
    private TipoResultado tipoResultado;
    private String unidadMedida;
    private BigDecimal valorMinimo;
    private BigDecimal valorMaximo;
    private BigDecimal valorCriticoMin;
    private BigDecimal valorCriticoMax;
    private Integer ordenVisualizacion;
    private String observaciones;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}