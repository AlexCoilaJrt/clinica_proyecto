package com.pe.laboratorio.results.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateResultRequest {
    private Long orderExamId;
    private String value;
    private String unit;
    private String referenceRange;
    private String observations;
}
