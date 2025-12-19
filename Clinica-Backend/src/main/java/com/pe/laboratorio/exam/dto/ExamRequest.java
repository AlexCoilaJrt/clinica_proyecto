package com.pe.laboratorio.exam.dto;

import com.pe.laboratorio.exam.enums.TipoMuestra;
import com.pe.laboratorio.exam.enums.TipoResultado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamRequest {

    @NotBlank(message = "El código es obligatorio")
    @Size(min = 2, max = 20, message = "El código debe tener entre 2 y 20 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
    private String nombre;

    @NotNull(message = "El área es obligatoria")
    private Long areaId;

    @NotNull(message = "El tipo de examen es obligatorio")
    private Long tipoExamenId;

    private String metodo;

    private String unidadMedida;

    private TipoMuestra tipoMuestra;

    @NotNull(message = "El tipo de resultado es obligatorio")
    private TipoResultado tipoResultado;

    private BigDecimal precio;

    private BigDecimal valorMinimo;

    private BigDecimal valorMaximo;

    private BigDecimal valorCriticoMin;

    private BigDecimal valorCriticoMax;

    private Integer tiempoEntrega;

    private String indicaciones;

    private Boolean esPerfil = false;
}