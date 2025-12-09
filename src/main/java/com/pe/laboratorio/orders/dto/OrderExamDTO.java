package com.pe.laboratorio.orders.dto;

import com.pe.laboratorio.orders.entity.OrderStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderExamDTO {
    private Long id;
    private Long examId;
    private String examName;
    private OrderStatus status;
    private String priority;
}
