package com.pe.laboratorio.security.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Entidad para registrar intentos fallidos de autenticación
 * Tabla: intruso
 */
@Entity
@Table(name = "intruso", indexes = {
        @Index(name = "idx_intruso_username", columnList = "username"),
        @Index(name = "idx_intruso_ip", columnList = "ipAddress"),
        @Index(name = "idx_intruso_attempt_time", columnList = "attemptTime")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntrusionAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de usuario con el que se intentó acceder
     */
    @Column(nullable = false)
    private String username;

    /**
     * Dirección IP desde donde se realizó el intento
     */
    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    /**
     * User-Agent del navegador/dispositivo
     */
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    /**
     * Fecha y hora del intento fallido
     */
    @Column(name = "attempt_time", nullable = false)
    private LocalDateTime attemptTime;

    /**
     * Razón del fallo de autenticación
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "failure_reason", nullable = false, length = 50)
    private FailureReason failureReason;

    /**
     * Número de intentos consecutivos desde esta IP
     */
    @Column(name = "consecutive_attempts")
    @Builder.Default
    private Integer consecutiveAttempts = 1;

    /**
     * Indica si este intento resultó en un bloqueo
     */
    @Column(name = "caused_block")
    @Builder.Default
    private boolean causedBlock = false;

    /**
     * Observaciones adicionales
     */
    @Column(columnDefinition = "TEXT")
    private String remarks;

    /**
     * Método pre-persist para establecer valores por defecto
     */
    @PrePersist
    protected void onCreate() {
        if (attemptTime == null) {
            attemptTime = LocalDateTime.now();
        }
    }
}
