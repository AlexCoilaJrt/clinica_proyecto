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
        // Extraer información del cliente
        String ipAddress = clientInfoExtractor.getClientIp(request);
        String userAgent = clientInfoExtractor.getUserAgent(request);

        // VALIDACIÓN DE SEGURIDAD: Verificar si la IP está bloqueada
        if (securityMonitorService.isIpBlocked(ipAddress)) {
            securityMonitorService.registerFailedAttempt(
                    loginDto.getUsername(), ipAddress, userAgent, FailureReason.IP_BLOCKED);
            throw new AuthException("IP bloqueada temporalmente por intentos sospechosos. Intente más tarde.");
        }

        var userOptional = userRepository.findByUsername(loginDto.getUsername());

        if (userOptional.isEmpty()) {
            // REGISTRAR INTENTO FALLIDO: Usuario no encontrado
            securityMonitorService.registerFailedAttempt(
                    loginDto.getUsername(), ipAddress, userAgent, FailureReason.USER_NOT_FOUND);
            throw new AuthException("Credenciales inválidas (usuario o contraseña incorrectos).");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            // REGISTRAR INTENTO FALLIDO: Contraseña incorrecta
            securityMonitorService.registerFailedAttempt(
                    loginDto.getUsername(), ipAddress, userAgent, FailureReason.INVALID_CREDENTIALS);
            throw new AuthException("Credenciales inválidas...");
        }

        if (!user.isEnabled()) {
            // REGISTRAR INTENTO FALLIDO: Cuenta bloqueada
            securityMonitorService.registerFailedAttempt(
                    loginDto.getUsername(), ipAddress, userAgent, FailureReason.ACCOUNT_BLOCKED);
            throw new AuthException("La cuenta ha sido bloqueada. Contacte al administrador.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        } catch (AuthenticationException e) {
            // REGISTRAR INTENTO FALLIDO: Error de autenticación
            securityMonitorService.registerFailedAttempt(
                    loginDto.getUsername(), ipAddress, userAgent, FailureReason.AUTHENTICATION_ERROR);
            throw new AuthException("Fallo interno de autenticación.");
        }

        // Generar token JWT
        String token = jwtService.generateToken(user.getUsername());

        // REGISTRAR SESIÓN EXITOSA con análisis de seguridad
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
}