package com.pe.laboratorio.security.entity;

/**
 * Estados posibles de una sesión de usuario
 */
public enum SessionStatus {
    /**
     * Sesión actualmente activa
     */
    ACTIVE,

    /**
     * Sesión cerrada normalmente
     */
    CLOSED,

    /**
     * Sesión marcada como sospechosa por comportamiento anómalo
     */
    SUSPICIOUS,

    /**
     * Sesión expirada por timeout
     */
    EXPIRED
}
