package com.pe.laboratorio.orders.service;

import com.pe.laboratorio.orders.dto.CreateOrderRequest;
import com.pe.laboratorio.orders.dto.LaboratoryOrderDTO;
import com.pe.laboratorio.orders.dto.UpdateOrderRequest;
import com.pe.laboratorio.orders.entity.OrderStatus;
import java.util.List;

public interface OrderService {
    LaboratoryOrderDTO createOrder(CreateOrderRequest request, String username);

    LaboratoryOrderDTO getOrderById(Long id);

    List<LaboratoryOrderDTO> getAllOrders();

    List<LaboratoryOrderDTO> getOrdersByPatient(Long patientId);

    List<LaboratoryOrderDTO> getOrdersByStatus(OrderStatus status);

    LaboratoryOrderDTO updateOrderStatus(Long id, UpdateOrderRequest request);

    void deleteOrder(Long id);
}
