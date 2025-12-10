package com.pe.laboratorio.orders.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.pe.laboratorio.orders.entity.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaboratoryOrderDTO {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdDate;
    private OrderStatus status;
    private List<OrderExamDTO> orderExams;
    private String observations;
}
