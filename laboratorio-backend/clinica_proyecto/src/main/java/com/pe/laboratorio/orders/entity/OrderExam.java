package com.pe.laboratorio.orders.entity;

import com.pe.laboratorio.catalogs.entity.Exam;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_exams")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderExam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private LaboratoryOrder order;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private String priority; // NORMAL, URGENTE

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = OrderStatus.PENDIENTE;
        }
        if (priority == null) {
            priority = "NORMAL";
        }
    }
}
