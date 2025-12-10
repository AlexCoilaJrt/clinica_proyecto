package com.pe.laboratorio.security.repository;

import com.pe.laboratorio.security.entity.Session;
import com.pe.laboratorio.security.entity.SessionStatus;
import com.pe.laboratorio.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar sesiones de usuarios
 */
@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    /**
     * Buscar sesión por token
     */
    Optional<Session> findBySessionToken(String sessionToken);

    /**
     * Buscar sesiones activas de un usuario
     */
    List<Session> findByUserAndStatus(User user, SessionStatus status);

    /**
     * Contar sesiones activas de un usuario
     */
    long countByUserAndStatus(User user, SessionStatus status);

    /**
     * Buscar sesiones de un usuario desde una IP específica
     */
    List<Session> findByUserAndIpAddress(User user, String ipAddress);

    /**
     * Buscar última sesión exitosa de un usuario
     */
    @Query("SELECT s FROM Session s WHERE s.user = :user ORDER BY s.loginTime DESC LIMIT 1")
    Optional<Session> findLastSessionByUser(@Param("user") User user);

    /**
     * Buscar sesiones sospechosas en un rango de tiempo
     */
    List<Session> findByIsSuspiciousAndLoginTimeBetween(
            boolean isSuspicious,
            LocalDateTime startTime,
            LocalDateTime endTime);

    /**
     * Buscar sesiones activas desde una IP específica
     */
    List<Session> findByIpAddressAndStatus(String ipAddress, SessionStatus status);

    /**
     * Verificar si existe sesión activa desde diferente IP en tiempo reciente
     */
    @Query("SELECT COUNT(s) > 0 FROM Session s WHERE s.user = :user " +
            "AND s.ipAddress != :currentIp " +
            "AND s.status = 'ACTIVE' " +
            "AND s.loginTime >= :sinceTime")
    boolean existsRecentSessionFromDifferentIp(
            @Param("user") User user,
            @Param("currentIp") String currentIp,
            @Param("sinceTime") LocalDateTime sinceTime);
}
