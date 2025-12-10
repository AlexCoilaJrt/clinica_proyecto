package com.pe.laboratorio.orders.repository;

import com.pe.laboratorio.orders.entity.OrderExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderExamRepository extends JpaRepository<OrderExam, Long> {
    List<OrderExam> findByOrderId(Long orderId);
}
