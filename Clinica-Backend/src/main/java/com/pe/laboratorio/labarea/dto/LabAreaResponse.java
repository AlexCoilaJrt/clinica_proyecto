package com.pe.laboratorio.labarea.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabAreaResponse {
    private Long id;
    private String codigo;
    private String descripcion;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}