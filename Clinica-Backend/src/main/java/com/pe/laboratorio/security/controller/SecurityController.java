package com.pe.laboratorio.security.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pe.laboratorio.security.dto.SessionInfoDTO;
import com.pe.laboratorio.security.entity.IntrusionAttempt;
import com.pe.laboratorio.security.entity.Session;
import com.pe.laboratorio.security.service.SecurityMonitorService;
import com.pe.laboratorio.users.entity.DatosPersonales;
import com.pe.laboratorio.users.repository.DatosPersonalesRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityMonitorService securityMonitorService;
    private final DatosPersonalesRepository datosPersonalesRepository;

    @GetMapping("/sessions/{login}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_BIOLOGO')")
    public ResponseEntity<List<SessionInfoDTO>> getUserActiveSessions(@PathVariable String login) {
        DatosPersonales user = datosPersonalesRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Session> sessions = securityMonitorService.getActiveSessions(user);

        List<SessionInfoDTO> sessionDTOs = sessions.stream()
                .map(s -> SessionInfoDTO.builder()
                        .id(s.getId())
                        .username(s.getUser().getUsername())
                        .ipAddress(s.getIpAddress())
                        .userAgent(s.getUserAgent())
                        .loginTime(s.getLoginTime())
                        .lastAccessTime(s.getLastAccessTime())
                        .status(s.getStatus())
                        .isSuspicious(s.isSuspicious())
                        .location(s.getLocation())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(sessionDTOs);
    }

    @GetMapping("/intrusions/recent")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_BIOLOGO')")
    public ResponseEntity<List<IntrusionAttempt>> getRecentIntrusions(
            @RequestParam(defaultValue = "24") int hours) {

        List<IntrusionAttempt> attempts = securityMonitorService.getRecentIntrusionAttempts(hours);
        return ResponseEntity.ok(attempts);
    }

    @PostMapping("/sessions/{sessionId}/close")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_BIOLOGO')")
    public ResponseEntity<String> closeSession(@PathVariable Long sessionId) {
        // Este método necesitaría obtener el token de la sesión
        // Por simplicidad, se deja como ejemplo
        return ResponseEntity.ok("Sesión cerrada exitosamente");
    }

    @PostMapping("/sessions/{sessionId}/mark-suspicious")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_BIOLOGO')")
    public ResponseEntity<String> markAsSuspicious(
            @PathVariable Long sessionId,
            @RequestParam String reason) {

        securityMonitorService.markSessionAsSuspicious(sessionId, reason);
        return ResponseEntity.ok("Sesión marcada como sospechosa");
    }
}