package com.pe.laboratorio.results.entity;

import com.pe.laboratorio.orders.entity.OrderExam;
import com.pe.laboratorio.users.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_exam_id", nullable = false)
    private OrderExam orderExam;

    @Column(nullable = false)
    private String value;

    private String unit;

    private String referenceRange;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValidationStatus validationStatus;

    @ManyToOne
    @JoinColumn(name = "entered_by")
    private User enteredBy;

    private LocalDateTime enteredDate;

    @ManyToOne
    @JoinColumn(name = "primary_validator_id")
    private User primaryValidator;

    private LocalDateTime primaryValidationDate;

    @ManyToOne
    @JoinColumn(name = "final_validator_id")
    private User finalValidator;

    private LocalDateTime finalValidationDate;

    @Column(length = 1000)
    private String observations;

    @PrePersist
    protected void onCreate() {
        enteredDate = LocalDateTime.now();
        if (validationStatus == null) {
            validationStatus = ValidationStatus.PENDIENTE;
        }
    }
}
