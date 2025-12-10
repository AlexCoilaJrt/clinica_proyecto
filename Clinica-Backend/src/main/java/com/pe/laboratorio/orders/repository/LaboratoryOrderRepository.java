package com.pe.laboratorio.orders.repository;

import com.pe.laboratorio.orders.entity.LaboratoryOrder;
import com.pe.laboratorio.orders.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LaboratoryOrderRepository extends JpaRepository<LaboratoryOrder, Long> {
    List<LaboratoryOrder> findByPatientId(Long patientId);

    List<LaboratoryOrder> findByStatus(OrderStatus status);

    List<LaboratoryOrder> findByCreatedById(Long userId);
}
