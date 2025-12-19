package com.pe.laboratorio.exam.entity;

import com.pe.laboratorio.exam.enums.TipoMuestra;
import com.pe.laboratorio.exam.enums.TipoResultado;
import com.pe.laboratorio.examtype.entity.ExamType;
import com.pe.laboratorio.labarea.entity.LabArea;
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
@Table(name = "lab_examenes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private LabArea area;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_examen_id", nullable = false)
    private ExamType tipoExamen;

    @Column(length = 200)
    private String metodo;

    @Column(length = 50)
    private String unidadMedida;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_muestra", length = 50)
    private TipoMuestra tipoMuestra;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_resultado", nullable = false, length = 50)
    private TipoResultado tipoResultado;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "valor_minimo", precision = 10, scale = 2)
    private BigDecimal valorMinimo;

    @Column(name = "valor_maximo", precision = 10, scale = 2)
    private BigDecimal valorMaximo;

    @Column(name = "valor_critico_min", precision = 10, scale = 2)
    private BigDecimal valorCriticoMin;

    @Column(name = "valor_critico_max", precision = 10, scale = 2)
    private BigDecimal valorCriticoMax;

    @Column(name = "tiempo_entrega")
    private Integer tiempoEntrega;

    @Column(length = 1000)
    private String indicaciones;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "es_perfil", nullable = false)
    private Boolean esPerfil = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}