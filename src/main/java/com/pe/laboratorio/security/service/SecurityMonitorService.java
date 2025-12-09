package com.pe.laboratorio.security.service;

import com.pe.laboratorio.security.entity.FailureReason;
import com.pe.laboratorio.security.entity.IntrusionAttempt;
import com.pe.laboratorio.security.entity.Session;
import com.pe.laboratorio.security.entity.SessionStatus;
import com.pe.laboratorio.security.repository.IntrusionAttemptRepository;
import com.pe.laboratorio.security.repository.SessionRepository;
import com.pe.laboratorio.users.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para monitoreo y análisis de seguridad
 * Detecta accesos sospechosos, intentos de intrusión y comportamientos anómalos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityMonitorService {

    private final SessionRepository sessionRepository;
    private final IntrusionAttemptRepository intrusionAttemptRepository;

    // Configuraciones de seguridad (puedes moverlas a application.properties)
    @Value("${security.max-failed-attempts:5}")
    private int maxFailedAttempts;

    @Value("${security.block-duration-minutes:15}")
    private int blockDurationMinutes;

    @Value("${security.suspicious-ip-change-hours:1}")
    private int suspiciousIpChangeHours;

    @Value("${security.max-concurrent-sessions:3}")
    private int maxConcurrentSessions;

    /**
     * Verifica si una IP está bloqueada por intentos excesivos
     */
    public boolean isIpBlocked(String ipAddress) {
        LocalDateTime sinceTime = LocalDateTime.now().minusMinutes(blockDurationMinutes);
        long failedAttempts = intrusionAttemptRepository
                .countByIpAddressAndAttemptTimeAfter(ipAddress, sinceTime);

        boolean isBlocked = failedAttempts >= maxFailedAttempts;

        if (isBlocked) {
            log.warn("IP bloqueada por intentos excesivos: {} ({} intentos en {} minutos)",
                    ipAddress, failedAttempts, blockDurationMinutes);
        }

        return isBlocked;
    }

    /**
     * Verifica si un usuario tiene intentos fallidos excesivos
     */
    public boolean hasExcessiveFailedAttempts(String username) {
        LocalDateTime sinceTime = LocalDateTime.now().minusMinutes(blockDurationMinutes);
        long failedAttempts = intrusionAttemptRepository
                .countByUsernameAndAttemptTimeAfter(username, sinceTime);

        boolean hasExcessive = failedAttempts >= maxFailedAttempts;

        if (hasExcessive) {
            log.warn("Usuario con intentos excesivos: {} ({} intentos en {} minutos)",
                    username, failedAttempts, blockDurationMinutes);
        }

        return hasExcessive;
    }

    /**
     * Registra un intento fallido de autenticación
     */
    @Transactional
    public void registerFailedAttempt(String username, String ipAddress, String userAgent, FailureReason reason) {
        // Contar intentos consecutivos
        LocalDateTime sinceTime = LocalDateTime.now().minusMinutes(blockDurationMinutes);
        long consecutiveAttempts = intrusionAttemptRepository
                .countByIpAddressAndAttemptTimeAfter(ipAddress, sinceTime) + 1;

        // Verificar si este intento causará un bloqueo
        boolean willCauseBlock = consecutiveAttempts >= maxFailedAttempts;

        IntrusionAttempt attempt = IntrusionAttempt.builder()
                .username(username)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .failureReason(reason)
                .consecutiveAttempts((int) consecutiveAttempts)
                .causedBlock(willCauseBlock)
                .attemptTime(LocalDateTime.now())
                .build();

        intrusionAttemptRepository.save(attempt);

        log.info("Intento fallido registrado - Usuario: {}, IP: {}, Razón: {}, Intento #{}",
                username, ipAddress, reason, consecutiveAttempts);

        if (willCauseBlock) {
            log.error("ALERTA: IP {} bloqueada por {} intentos fallidos", ipAddress, consecutiveAttempts);
        }
    }

    /**
     * Registra una sesión exitosa
     */
    @Transactional
    public Session registerSuccessfulLogin(User user, String ipAddress, String userAgent, String sessionToken) {
        // Verificar si es un acceso sospechoso
        boolean isSuspicious = isSuspiciousLogin(user, ipAddress);

        Session session = Session.builder()
                .user(user)
                .sessionToken(sessionToken)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .loginTime(LocalDateTime.now())
                .lastAccessTime(LocalDateTime.now())
                .status(SessionStatus.ACTIVE)
                .isSuspicious(isSuspicious)
                .build();

        if (isSuspicious) {
            session.setRemarks("Acceso desde IP diferente a la habitual");
            log.warn("SESIÓN SOSPECHOSA: Usuario {} desde IP {} (User-Agent: {})",
                    user.getUsername(), ipAddress, userAgent);
        }

        Session savedSession = sessionRepository.save(session);

        // Limpiar intentos fallidos después de login exitoso
        cleanFailedAttemptsForUser(user.getUsername(), ipAddress);

        log.info("Sesión registrada - Usuario: {}, IP: {}, Sospechosa: {}",
                user.getUsername(), ipAddress, isSuspicious);

        return savedSession;
    }

    /**
     * Verifica si un login es sospechoso
     */
    public boolean isSuspiciousLogin(User user, String currentIp) {
        // Verificar si existe sesión reciente desde diferente IP
        LocalDateTime recentTime = LocalDateTime.now().minusHours(suspiciousIpChangeHours);

        boolean differentIpRecently = sessionRepository
                .existsRecentSessionFromDifferentIp(user, currentIp, recentTime);

        if (differentIpRecently) {
            log.warn("Detectado cambio de IP sospechoso para usuario {}: nueva IP {}",
                    user.getUsername(), currentIp);
            return true;
        }

        // Verificar sesiones concurrentes
        long activeSessions = sessionRepository.countByUserAndStatus(user, SessionStatus.ACTIVE);

        if (activeSessions >= maxConcurrentSessions) {
            log.warn("Usuario {} tiene {} sesiones activas (máximo: {})",
                    user.getUsername(), activeSessions, maxConcurrentSessions);
            return true;
        }

        return false;
    }

    /**
     * Limpia intentos fallidos después de un login exitoso
     */
    @Transactional
    public void cleanFailedAttemptsForUser(String username, String ipAddress) {
        LocalDateTime sinceTime = LocalDateTime.now().minusMinutes(blockDurationMinutes);
        List<IntrusionAttempt> attempts = intrusionAttemptRepository
                .findByUsernameAndAttemptTimeAfterOrderByAttemptTimeDesc(username, sinceTime);

        if (!attempts.isEmpty()) {
            log.info("Limpiando {} intentos fallidos para usuario {} desde IP {}",
                    attempts.size(), username, ipAddress);
        }
    }

    /**
     * Actualiza el último acceso de una sesión
     */
    @Transactional
    public void updateLastAccess(String sessionToken) {
        sessionRepository.findBySessionToken(sessionToken)
                .ifPresent(session -> {
                    session.setLastAccessTime(LocalDateTime.now());
                    sessionRepository.save(session);
                });
    }

    /**
     * Cierra una sesión
     */
    @Transactional
    public void closeSession(String sessionToken) {
        sessionRepository.findBySessionToken(sessionToken)
                .ifPresent(session -> {
                    session.setStatus(SessionStatus.CLOSED);
                    session.setLastAccessTime(LocalDateTime.now());
                    sessionRepository.save(session);
                    log.info("Sesión cerrada - Token: {}", sessionToken.substring(0, 20) + "...");
                });
    }

    /**
     * Marca una sesión como sospechosa
     */
    @Transactional
    public void markSessionAsSuspicious(Long sessionId, String reason) {
        sessionRepository.findById(sessionId)
                .ifPresent(session -> {
                    session.setSuspicious(true);
                    session.setRemarks(reason);
                    session.setStatus(SessionStatus.SUSPICIOUS);
                    sessionRepository.save(session);
                    log.error("ALERTA: Sesión {} marcada como sospechosa: {}", sessionId, reason);
                });
    }

    /**
     * Obtiene sesiones activas de un usuario
     */
    public List<Session> getActiveSessions(User user) {
        return sessionRepository.findByUserAndStatus(user, SessionStatus.ACTIVE);
    }

    /**
     * Obtiene intentos de intrusión recientes
     */
    public List<IntrusionAttempt> getRecentIntrusionAttempts(int hours) {
        LocalDateTime sinceTime = LocalDateTime.now().minusHours(hours);
        return intrusionAttemptRepository
                .findByIpAddressAndAttemptTimeAfterOrderByAttemptTimeDesc("*", sinceTime);
    }
}
