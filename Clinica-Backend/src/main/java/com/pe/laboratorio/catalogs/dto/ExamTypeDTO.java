package com.pe.laboratorio.catalogs.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExamTypeDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean isEnabled;
}