package com.pe.laboratorio.results.repository;

import com.pe.laboratorio.results.entity.ExamResult;
import com.pe.laboratorio.results.entity.ValidationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    List<ExamResult> findByValidationStatus(ValidationStatus status);

    Optional<ExamResult> findByOrderExamId(Long orderExamId);

    @Query("SELECT r FROM ExamResult r WHERE r.orderExam.order.id = :orderId")
    List<ExamResult> findByOrderId(Long orderId);

    @Query("SELECT r FROM ExamResult r WHERE r.validationStatus = 'PENDIENTE' OR r.validationStatus = 'VALIDADO_PRIMARIO'")
    List<ExamResult> findPendingValidation();
}
