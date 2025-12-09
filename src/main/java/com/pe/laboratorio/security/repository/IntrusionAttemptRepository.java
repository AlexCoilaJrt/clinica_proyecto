package com.pe.laboratorio.security.repository;

import com.pe.laboratorio.security.entity.FailureReason;
import com.pe.laboratorio.security.entity.IntrusionAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para gestionar intentos de intrusión
 */
@Repository
public interface IntrusionAttemptRepository extends JpaRepository<IntrusionAttempt, Long> {

    /**
     * Contar intentos fallidos desde una IP en un período de tiempo
     */
    long countByIpAddressAndAttemptTimeAfter(String ipAddress, LocalDateTime sinceTime);

    /**
     * Contar intentos fallidos para un username en un período de tiempo
     */
    long countByUsernameAndAttemptTimeAfter(String username, LocalDateTime sinceTime);

    /**
     * Buscar intentos fallidos recientes desde una IP
     */
    List<IntrusionAttempt> findByIpAddressAndAttemptTimeAfterOrderByAttemptTimeDesc(
            String ipAddress,
            LocalDateTime sinceTime);

    /**
     * Buscar intentos fallidos recientes para un username
     */
    List<IntrusionAttempt> findByUsernameAndAttemptTimeAfterOrderByAttemptTimeDesc(
            String username,
            LocalDateTime sinceTime);

    /**
     * Buscar intentos por razón de fallo en un período
     */
    List<IntrusionAttempt> findByFailureReasonAndAttemptTimeBetween(
            FailureReason reason,
            LocalDateTime startTime,
            LocalDateTime endTime);

    /**
     * Eliminar intentos antiguos (limpieza de datos)
     */
    void deleteByAttemptTimeBefore(LocalDateTime beforeTime);

    /**
     * Verificar si una IP está bloqueada (muchos intentos recientes)
     */
    @Query("SELECT COUNT(i) >= :threshold FROM IntrusionAttempt i " +
            "WHERE i.ipAddress = :ipAddress " +
            "AND i.attemptTime >= :sinceTime")
    boolean isIpBlocked(
            @Param("ipAddress") String ipAddress,
            @Param("sinceTime") LocalDateTime sinceTime,
            @Param("threshold") long threshold);

    /**
     * Verificar si un username tiene intentos excesivos
     */
    @Query("SELECT COUNT(i) >= :threshold FROM IntrusionAttempt i " +
            "WHERE i.username = :username " +
            "AND i.attemptTime >= :sinceTime")
    boolean hasExcessiveFailedAttempts(
            @Param("username") String username,
            @Param("sinceTime") LocalDateTime sinceTime,
            @Param("threshold") long threshold);
}
