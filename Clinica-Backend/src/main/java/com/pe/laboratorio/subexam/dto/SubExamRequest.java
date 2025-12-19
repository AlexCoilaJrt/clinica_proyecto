package com.pe.laboratorio.subexam.dto;

import java.math.BigDecimal;

import com.pe.laboratorio.exam.enums.TipoResultado;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubExamRequest {

    @NotNull(message = "El examen padre es obligatorio")
    private Long examenId;

    @NotBlank(message = "El código es obligatorio")
    @Size(min = 2, max = 20)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 200)
    private String nombre;

    @NotNull(message = "El tipo de resultado es obligatorio")
    private TipoResultado tipoResultado;

    private String unidadMedida;

    private BigDecimal valorMinimo;

    private BigDecimal valorMaximo;

    private BigDecimal valorCriticoMin;

    private BigDecimal valorCriticoMax;

    @NotNull(message = "El orden de visualización es obligatorio")
    private Integer ordenVisualizacion;

    private String observaciones;
}