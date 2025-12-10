package com.pe.laboratorio.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pe.laboratorio.auth.service.dto.AuthResponse;
import com.pe.laboratorio.auth.service.dto.LoginDTO;
import com.pe.laboratorio.auth.service.dto.RegisterDTO;
import com.pe.laboratorio.exception.AuthException;
import com.pe.laboratorio.security.entity.FailureReason;
import com.pe.laboratorio.security.service.SecurityMonitorService;
import com.pe.laboratorio.security.util.ClientInfoExtractor;
import com.pe.laboratorio.users.entity.User;
import com.pe.laboratorio.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final SecurityMonitorService securityMonitorService;
    private final ClientInfoExtractor clientInfoExtractor;

    public AuthResponse login(LoginDTO loginDto, HttpServletRequest request) {

        String ipAddress = clientInfoExtractor.getClientIp(request);
        String userAgent = clientInfoExtractor.getUserAgent(request);

        if (securityMonitorService.isIpBlocked(ipAddress)) {
            securityMonitorService.registerFailedAttempt(
                    loginDto.getUsername(), ipAddress, userAgent, FailureReason.IP_BLOCKED);
            throw new AuthException("IP bloqueada temporalmente por intentos sospechosos. Intente más tarde.");
        }

        var userOptional = userRepository.findByUsername(loginDto.getUsername());

        if (userOptional.isEmpty()) {

            securityMonitorService.registerFailedAttempt(
                    loginDto.getUsername(), ipAddress, userAgent, FailureReason.USER_NOT_FOUND);
            throw new AuthException("Credenciales inválidas (usuario o contraseña incorrectos).");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {

            securityMonitorService.registerFailedAttempt(
                    loginDto.getUsername(), ipAddress, userAgent, FailureReason.INVALID_CREDENTIALS);

            int remainingAttempts = securityMonitorService.getRemainingAttempts(ipAddress);

            LocalDateTime unblockTime = remainingAttempts == 0
                    ? securityMonitorService.getUnblockTime(ipAddress)
                    : null;

            String message = generateFailureMessage(remainingAttempts);

            throw new AuthException(message, remainingAttempts, unblockTime);
        }

        if (!user.isEnabled()) {
            securityMonitorService.registerFailedAttempt(
                    loginDto.getUsername(), ipAddress, userAgent, FailureReason.ACCOUNT_BLOCKED);
            throw new AuthException("La cuenta ha sido bloqueada. Contacte al administrador.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        } catch (AuthenticationException e) {
            securityMonitorService.registerFailedAttempt(
                    loginDto.getUsername(), ipAddress, userAgent, FailureReason.AUTHENTICATION_ERROR);
            throw new AuthException("Fallo interno de autenticación.");
        }

        String token = jwtService.generateToken(user.getUsername());

        securityMonitorService.registerSuccessfulLogin(user, ipAddress, userAgent, token);

        return new AuthResponse(token, "Sesión iniciada correctamente", user.getRole());
    }

    public AuthResponse register(RegisterDTO registerDto) {
        if (userRepository.findByUsername(registerDto.getUsername()).isPresent()) {
            throw new AuthException("El nombre de usuario ya existe. Elija otro.");
        }

        User newUser = new User();
        newUser.setUsername(registerDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        String assignedRole = registerDto.getRole() != null ? registerDto.getRole() : "ROLE_PACIENTE";
        newUser.setRole(assignedRole);

        userRepository.save(newUser);

        String token = jwtService.generateToken(newUser.getUsername());
        return new AuthResponse(token, "Usuario registrado exitosamente", assignedRole);
    }

    private String generateFailureMessage(int remainingAttempts) {
        if (remainingAttempts == 0) {
            return "🚫 Intentos agotados. Tu IP ha sido bloqueada temporalmente por seguridad. " +
                    "Intenta nuevamente en 15 minutos o contacta al administrador.";
        } else if (remainingAttempts == 1) {
            return "⚠️ ÚLTIMO INTENTO. Credenciales inválidas. " +
                    "Si fallas nuevamente, tu IP será bloqueada por 15 minutos.";
        } else if (remainingAttempts == 2) {
            return "⚠️ Credenciales inválidas. Te quedan " + remainingAttempts +
                    " intentos antes del bloqueo temporal.";
        } else {
            return "Credenciales inválidas. Verifica tu usuario y contraseña.";
        }
    }
}