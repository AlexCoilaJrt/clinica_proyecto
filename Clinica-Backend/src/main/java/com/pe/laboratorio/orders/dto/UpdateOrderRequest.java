package com.pe.laboratorio.orders.dto;

import com.pe.laboratorio.orders.entity.OrderStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderRequest {
    private OrderStatus status;
}
