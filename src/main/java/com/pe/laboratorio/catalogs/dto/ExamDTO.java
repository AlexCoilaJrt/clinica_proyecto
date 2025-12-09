package com.pe.laboratorio.catalogs.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExamDTO {
    private Long id;
    private String name;
    private String description;
    private Long areaId;
    private Long examTypeId;
    private AreaDTO area;
    private ExamTypeDTO examType;
    private Boolean isEnabled;
}