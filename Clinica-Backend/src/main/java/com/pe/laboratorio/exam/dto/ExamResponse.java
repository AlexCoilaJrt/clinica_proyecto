package com.pe.laboratorio.exam.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.pe.laboratorio.exam.enums.TipoMuestra;
import com.pe.laboratorio.exam.enums.TipoResultado;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamResponse {
    private Long id;
    private String codigo;
    private String nombre;
    private Long areaId;
    private String areaNombre;
    private String areaCodigo;
    private Long tipoExamenId;
    private String tipoExamenNombre;
    private String metodo;
    private String unidadMedida;
    private TipoMuestra tipoMuestra;
    private TipoResultado tipoResultado;
    private BigDecimal precio;
    private BigDecimal valorMinimo;
    private BigDecimal valorMaximo;
    private BigDecimal valorCriticoMin;
    private BigDecimal valorCriticoMax;
    private Integer tiempoEntrega;
    private String indicaciones;
    private Boolean active;
    private Boolean esPerfil;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}