package com.pe.laboratorio.subexam.entity;

import com.pe.laboratorio.exam.entity.Exam;
import com.pe.laboratorio.exam.enums.TipoResultado;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_sub_examenes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubExam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examen_id", nullable = false)
    private Exam examen; // Examen padre

    @Column(unique = true, nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_resultado", nullable = false, length = 50)
    private TipoResultado tipoResultado;

    @Column(length = 50)
    private String unidadMedida;

    @Column(name = "valor_minimo", precision = 10, scale = 2)
    private BigDecimal valorMinimo; // Valor normal mínimo

    @Column(name = "valor_maximo", precision = 10, scale = 2)
    private BigDecimal valorMaximo; // Valor normal máximo

    @Column(name = "valor_critico_min", precision = 10, scale = 2)
    private BigDecimal valorCriticoMin;

    @Column(name = "valor_critico_max", precision = 10, scale = 2)
    private BigDecimal valorCriticoMax;

    @Column(name = "orden_visualizacion", nullable = false)
    private Integer ordenVisualizacion = 0; // Orden de presentación

    @Column(length = 500)
    private String observaciones;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}