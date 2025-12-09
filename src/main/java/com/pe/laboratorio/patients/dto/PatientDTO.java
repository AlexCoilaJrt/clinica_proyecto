package com.pe.laboratorio.patients.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class PatientDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String dni;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private String address;
}