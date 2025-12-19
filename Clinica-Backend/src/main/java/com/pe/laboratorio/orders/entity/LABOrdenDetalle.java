package com.pe.laboratorio.orders.entity;

import com.pe.laboratorio.exam.entity.Exam;
import com.pe.laboratorio.users.entity.DatosPersonales;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_orden_detalle")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LABOrdenDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    @ToString.Exclude
    private LABOrden orden;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "equipo_id")
    private Long equipoId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoDetalle estado;

    @Column(length = 255)
    private String resultado;

    @Column(name = "valor_referencia", length = 100)
    private String valorReferencia;

    @Column(length = 50)
    private String unidad;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "valor_critico")
    @Builder.Default
    private Boolean valorCritico = false;

    @Column(name = "fuera_rango")
    @Builder.Default
    private Boolean fueraRango = false;

    @Column(name = "validado_primario")
    @Builder.Default
    private Boolean validadoPrimario = false;

    @Column(name = "validado_final")
    @Builder.Default
    private Boolean validadoFinal = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnologo_id")
    private DatosPersonales tecnologo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "biologo_id")
    private DatosPersonales biologo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procesado_por_id")
    private DatosPersonales procesadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validado_por_id")
    private DatosPersonales validadoPor;

    @Column(name = "fecha_procesamiento")
    private LocalDateTime fechaProcesamiento;

    @Column(name = "fecha_validacion_primaria")
    private LocalDateTime fechaValidacionPrimaria;

    @Column(name = "fecha_validacion_final")
    private LocalDateTime fechaValidacionFinal;

    // Campos de auditoría
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoDetalle.PENDIENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Enum
    public enum EstadoDetalle {
        PENDIENTE,
        EN_PROCESO,
        PROCESADO,
        VALIDADO
    }
}
