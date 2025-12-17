package com.pe.laboratorio.orders.entity;

import com.pe.laboratorio.patients.entity.Patient;
import com.pe.laboratorio.users.entity.DatosPersonales;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lab_orden")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LABOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id")
    private DatosPersonales medico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private DatosPersonales user;

    @Column(name = "numero_orden", length = 50)
    private String numeroOrden;

    @Column(name = "fecha_orden", nullable = false)
    private LocalDateTime fechaOrden;

    @Column(name = "diagnostico", length = 500)
    private String diagnostico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoOrden estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrioridadOrden prioridad;

    @Column(name = "tipo_atencion", length = 50)
    private String tipoAtencion;

    @Column(name = "tipo_muestra", length = 100)
    private String tipoMuestra;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "fecha_toma_muestra")
    private LocalDateTime fechaTomaMuestra;

    @Column(name = "fecha_procesamiento")
    private LocalDateTime fechaProcesamiento;

    @Column(name = "fecha_validacion")
    private LocalDateTime fechaValidacion;

    @Column(name = "fecha_entrega")
    private LocalDate fechaEntrega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validado_por_id")
    private DatosPersonales validadoPor;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LABOrdenDetalle> detalles = new ArrayList<>();

    // Campos de auditoría
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private DatosPersonales createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (fechaOrden == null) {
            fechaOrden = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoOrden.PENDIENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public void addDetalle(LABOrdenDetalle detalle) {
        detalles.add(detalle);
        detalle.setOrden(this);
    }

    public void removeDetalle(LABOrdenDetalle detalle) {
        detalles.remove(detalle);
        detalle.setOrden(null);
    }

    // Enums
    public enum EstadoOrden {
        PENDIENTE,
        EN_PROCESO,
        PROCESADO,
        VALIDADA,
        ENTREGADA
    }

    public enum PrioridadOrden {
        NORMAL,
        URGENTE,
        EMERGENCIA
    }
}
