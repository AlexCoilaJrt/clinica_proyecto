package com.pe.laboratorio.catalogs.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exam_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(name = "is_enabled")
    private Boolean isEnabled = true;
}