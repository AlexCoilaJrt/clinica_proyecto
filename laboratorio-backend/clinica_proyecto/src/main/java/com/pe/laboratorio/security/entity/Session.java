package com.pe.laboratorio.security.entity;

import com.pe.laboratorio.users.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Entidad para registrar sesiones de usuarios en el sistema
 * Tabla: sessiones
 */
@Entity
@Table(name = "sessiones", indexes = {
        @Index(name = "idx_sessiones_user_id", columnList = "user_id"),
        @Index(name = "idx_sessiones_ip", columnList = "ipAddress"),
        @Index(name = "idx_sessiones_status", columnList = "status"),
        @Index(name = "idx_sessiones_login_time", columnList = "loginTime")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario asociado a esta sesión
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Token de sesión JWT
     */
    @Column(name = "session_token", unique = true, nullable = false, length = 500)
    private String sessionToken;

    /**
     * Dirección IP desde donde se inició sesión
     */
    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    /**
     * User-Agent del navegador/dispositivo
     */
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    /**
     * Fecha y hora de inicio de sesión
     */
    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    /**
     * Fecha y hora del último acceso
     */
    @Column(name = "last_access_time")
    private LocalDateTime lastAccessTime;

    /**
     * Estado de la sesión
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SessionStatus status = SessionStatus.ACTIVE;

    /**
     * Ubicación geográfica estimada (opcional)
     */
    @Column(length = 100)
    private String location;

    /**
     * Indica si la sesión es considerada sospechosa
     */
    @Column(name = "is_suspicious", nullable = false)
    @Builder.Default
    private boolean isSuspicious = false;

    /**
     * Observaciones adicionales sobre la sesión
     */
    @Column(columnDefinition = "TEXT")
    private String remarks;

    /**
     * Método pre-persist para establecer valores por defecto
     */
    @PrePersist
    protected void onCreate() {
        if (loginTime == null) {
            loginTime = LocalDateTime.now();
        }
        if (lastAccessTime == null) {
            lastAccessTime = loginTime;
        }
    }
}
