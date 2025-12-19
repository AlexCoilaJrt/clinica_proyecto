package com.pe.laboratorio.examtype.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamTypeResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}