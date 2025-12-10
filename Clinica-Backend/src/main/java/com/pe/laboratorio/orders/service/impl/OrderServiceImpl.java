package com.pe.laboratorio.orders.service.impl;

import com.pe.laboratorio.catalogs.entity.Exam;
import com.pe.laboratorio.catalogs.repository.ExamRepository;
import com.pe.laboratorio.exception.ResourceNotFoundException;
import com.pe.laboratorio.orders.dto.CreateOrderRequest;
import com.pe.laboratorio.orders.dto.LaboratoryOrderDTO;
import com.pe.laboratorio.orders.dto.OrderExamDTO;
import com.pe.laboratorio.orders.dto.UpdateOrderRequest;
import com.pe.laboratorio.orders.entity.LaboratoryOrder;
import com.pe.laboratorio.orders.entity.OrderExam;
import com.pe.laboratorio.orders.entity.OrderStatus;
import com.pe.laboratorio.orders.repository.LaboratoryOrderRepository;
import com.pe.laboratorio.orders.repository.OrderExamRepository;
import com.pe.laboratorio.orders.service.OrderService;
import com.pe.laboratorio.patients.entity.Patient;
import com.pe.laboratorio.patients.repository.PatientRepository;
import com.pe.laboratorio.users.entity.User;
import com.pe.laboratorio.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final LaboratoryOrderRepository orderRepository;
    private final OrderExamRepository orderExamRepository;
    private final PatientRepository patientRepository;
    private final ExamRepository examRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public LaboratoryOrderDTO createOrder(CreateOrderRequest request, String username) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paciente no encontrado con ID: " + request.getPatientId()));

        User createdBy = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        LaboratoryOrder order = LaboratoryOrder.builder()
                .patient(patient)
                .createdBy(createdBy)
                .status(OrderStatus.PENDIENTE)
                .observations(request.getObservations())
                .build();

        order = orderRepository.save(order);

        for (Long examId : request.getExamIds()) {
            Exam exam = examRepository.findById(examId)
                    .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado con ID: " + examId));

            OrderExam orderExam = OrderExam.builder()
                    .order(order)
                    .exam(exam)
                    .status(OrderStatus.PENDIENTE)
                    .build();

            orderExamRepository.save(orderExam);
        }

        return mapToDTO(orderRepository.findById(order.getId()).orElseThrow());
    }

    @Override
    public LaboratoryOrderDTO getOrderById(Long id) {
        LaboratoryOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));
        return mapToDTO(order);
    }

    @Override
    public List<LaboratoryOrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LaboratoryOrderDTO> getOrdersByPatient(Long patientId) {
        return orderRepository.findByPatientId(patientId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LaboratoryOrderDTO> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LaboratoryOrderDTO updateOrderStatus(Long id, UpdateOrderRequest request) {
        LaboratoryOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));

        order.setStatus(request.getStatus());
        order = orderRepository.save(order);

        return mapToDTO(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Orden no encontrada con ID: " + id);
        }
        orderRepository.deleteById(id);
    }

    private LaboratoryOrderDTO mapToDTO(LaboratoryOrder order) {
        List<OrderExamDTO> orderExamDTOs = order.getOrderExams().stream()
                .map(oe -> OrderExamDTO.builder()
                        .id(oe.getId())
                        .examId(oe.getExam().getId())
                        .examName(oe.getExam().getName())
                        .status(oe.getStatus())
                        .priority(oe.getPriority())
                        .build())
                .collect(Collectors.toList());

        return LaboratoryOrderDTO.builder()
                .id(order.getId())
                .patientId(order.getPatient().getId())
                .patientName(order.getPatient().getFirstName() + " " + order.getPatient().getLastName())
                .createdById(order.getCreatedBy() != null ? order.getCreatedBy().getId() : null)
                .createdByName(order.getCreatedBy() != null ? order.getCreatedBy().getUsername() : null)
                .createdDate(order.getCreatedDate())
                .status(order.getStatus())
                .orderExams(orderExamDTOs)
                .observations(order.getObservations())
                .build();
    }
}
