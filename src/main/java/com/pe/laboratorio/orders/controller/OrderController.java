package com.pe.laboratorio.orders.controller;

import com.pe.laboratorio.orders.dto.CreateOrderRequest;
import com.pe.laboratorio.orders.dto.LaboratoryOrderDTO;
import com.pe.laboratorio.orders.dto.UpdateOrderRequest;
import com.pe.laboratorio.orders.entity.OrderStatus;
import com.pe.laboratorio.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<LaboratoryOrderDTO> createOrder(
            @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        LaboratoryOrderDTO created = orderService.createOrder(request, username);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<LaboratoryOrderDTO>> getAllOrders() {
        List<LaboratoryOrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LaboratoryOrderDTO> getOrderById(@PathVariable Long id) {
        LaboratoryOrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<LaboratoryOrderDTO>> getOrdersByPatient(@PathVariable Long patientId) {
        List<LaboratoryOrderDTO> orders = orderService.getOrdersByPatient(patientId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LaboratoryOrderDTO>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<LaboratoryOrderDTO> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<LaboratoryOrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderRequest request) {
        LaboratoryOrderDTO updated = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
