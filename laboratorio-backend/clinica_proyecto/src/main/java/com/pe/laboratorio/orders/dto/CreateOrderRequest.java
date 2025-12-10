package com.pe.laboratorio.orders.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private Long patientId;
    private List<Long> examIds;
    private String observations;
}
