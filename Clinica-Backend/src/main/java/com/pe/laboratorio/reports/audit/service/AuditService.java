package com.pe.laboratorio.reports.audit.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pe.laboratorio.reports.audit.entity.AuditLog;
import com.pe.laboratorio.reports.audit.repository.AuditRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;

    public void logAction(String module, String action, String details,
            String username, Long userId, String ipAddress,
            String status, String userAgent, String url, String method) {

        AuditLog log = AuditLog.builder()
                .module(module)
                .action(action)
                .details(details)
                .username(username)
                .userId(userId)
                .ipAddress(ipAddress)
                .status(status)
                .userAgent(userAgent)
                .url(url)
                .method(method)
                .timestamp(LocalDateTime.now())
                .build();
        auditRepository.save(log);
    }

    public List<AuditLog> getAllLogs() {
        return auditRepository.findAllByOrderByTimestampDesc();
    }

    public List<AuditLog> getLogsByUser(String username) {
        return auditRepository.findByUsernameOrderByTimestampDesc(username);
    }

    /**
     * Búsqueda avanzada para la bitácora
     */
    public List<AuditLog> searchLogs(String username, String action,
            LocalDateTime startDate, LocalDateTime endDate) {

        // Manejo de strings vacíos como null para ignorar filtro
        String userFilter = (username != null && !username.trim().isEmpty()) ? username : null;
        String actionFilter = (action != null && !action.equals("Todas")) ? action : null;

        return auditRepository.searchLogs(userFilter, actionFilter, startDate, endDate);
    }
}
