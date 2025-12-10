package com.pe.laboratorio.results.dto;

import com.pe.laboratorio.results.entity.ValidationStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultDTO {
    private Long id;
    private Long orderExamId;
    private String examName;
    private String patientName;
    private String value;
    private String unit;
    private String referenceRange;
    private ValidationStatus validationStatus;
    private String enteredByName;
    private LocalDateTime enteredDate;
    private String primaryValidatorName;
    private LocalDateTime primaryValidationDate;
    private String finalValidatorName;
    private LocalDateTime finalValidationDate;
    private String observations;
}
