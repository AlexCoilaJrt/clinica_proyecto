package com.pe.laboratorio.security.entity;

/**
 * Razones de fallo en intentos de autenticación
 */
public enum FailureReason {
    /**
     * Credenciales inválidas (usuario o contraseña incorrectos)
     */
    INVALID_CREDENTIALS,

    /**
     * Usuario no existe en el sistema
     */
    USER_NOT_FOUND,

    /**
     * Cuenta de usuario bloqueada
     */
    ACCOUNT_BLOCKED,

    /**
     * Error interno de autenticación
     */
    AUTHENTICATION_ERROR,

    /**
     * IP bloqueada por intentos excesivos
     */
    IP_BLOCKED
}
