package com.pe.laboratorio.auth.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.laboratorio.auth.service.dto.LoginRequest;
import com.pe.laboratorio.auth.service.dto.LoginResponse;
import com.pe.laboratorio.auth.service.dto.RegisterRequest;
import com.pe.laboratorio.auth.service.dto.RegisterResponse;
import com.pe.laboratorio.exception.AuthException;
import com.pe.laboratorio.exception.ValidationException;
import com.pe.laboratorio.roles.entity.Role;
import com.pe.laboratorio.roles.repository.RoleRepository;
import com.pe.laboratorio.users.entity.DatosPersonales;
import com.pe.laboratorio.users.repository.DatosPersonalesRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final DatosPersonalesRepository datosPersonalesRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) { // <- AÑADIDO: HttpServletRequest
                                                                                       // httpRequest
        log.info("Login attempt for user: {}", request.getUsername());

        // Validar credenciales con AuthenticationManager
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", request.getUsername());
            throw new AuthException("Credenciales inválidas (usuario o contraseña incorrectos)");
        }

        // Si quieres usar el httpRequest, puedes hacerlo aquí, por ejemplo:
        // log.info("Login from IP: {}", httpRequest.getRemoteAddr());

        // Cargar usuario con roles y permisos
        DatosPersonales user = datosPersonalesRepository.findByLoginWithRoles(request.getUsername())
                .orElseThrow(() -> new AuthException("Usuario no encontrado"));

        // Verificar si la cuenta está activa
        if (!user.getActive()) {
            log.warn("Attempt to login with inactive account: {}", request.getUsername());
            throw new AuthException("La cuenta ha sido bloqueada. Contacte al administrador.");
        }

        // Actualizar último login
        user.setLastLogin(LocalDateTime.now());
        datosPersonalesRepository.save(user);

        // Generar token JWT
        String token = jwtService.generateToken(user);

        // Obtener nombres de roles
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        // Obtener nombres de permisos
        Set<String> permissions = user.getPermissionNames();

        log.info("User {} logged in successfully with roles: {}", user.getUsername(), roleNames);

        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getNombre())
                .sexo(user.getSexo())
                .lastName(user.getApepat() + " " + user.getApemat())
                .roles(roleNames)
                .permissions(permissions)
                .build();
    }

    /**
     * Registro de nuevo usuario
     */
    public RegisterResponse register(RegisterRequest request) {
        log.info("Register attempt for username: {}", request.getUsername());

        // Validar que el login no exista
        if (datosPersonalesRepository.existsByLogin(request.getUsername())) {
            throw new ValidationException("El nombre de usuario ya existe. Elija otro.");
        }

        // Validar que el email no exista
        if (datosPersonalesRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("El email ya está registrado.");
        }

        // Crear nuevo usuario
        DatosPersonales newUser = DatosPersonales.builder()
                .login(request.getUsername())
                .email(request.getEmail())
                .passwd(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getFirstName())
                .apepat(request.getLastName())
                .fonLocal(request.getPhone())
                .active(true)
                .roles(new HashSet<>())
                .build();

        // Asignar roles
        Set<Role> assignedRoles = new HashSet<>();

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            // Asignar roles especificados
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByName(roleName.toUpperCase())
                        .orElseThrow(() -> new ValidationException("Rol no encontrado: " + roleName));
                assignedRoles.add(role);
            }
        } else {
            // Asignar rol por defecto: PACIENTE
            Role defaultRole = roleRepository.findByName("PACIENTE")
                    .orElseThrow(() -> new ValidationException("Rol PACIENTE no encontrado en el sistema"));
            assignedRoles.add(defaultRole);
        }

        newUser.setRoles(assignedRoles);

        // Guardar usuario
        DatosPersonales savedUser = datosPersonalesRepository.save(newUser);

        // Obtener nombres de roles asignados
        Set<String> roleNames = savedUser.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        log.info("User {} registered successfully with roles: {}", savedUser.getUsername(), roleNames);

        return RegisterResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .firstName(savedUser.getNombre())
                .lastName(savedUser.getApepat() + " " + savedUser.getApemat())
                .roles(roleNames)
                .message("Usuario registrado exitosamente")
                .build();
    }

    /**
     * Logout (opcional: implementar blacklist de tokens)
     */
    public void logout(String token) {
        // TODO: Implementar blacklist de tokens si es necesario
        log.info("User logged out");
    }

    /**
     * Obtener información del token (tiempo restante)
     */
    public com.pe.laboratorio.auth.service.dto.TokenInfoResponse getTokenInfo(String token) {
        return com.pe.laboratorio.auth.service.dto.TokenInfoResponse.builder()
                .timeRemainingMs(jwtService.getTimeRemaining(token))
                .timeRemainingSeconds(jwtService.getTimeRemainingInSeconds(token))
                .expirationTimeMs(jwtService.getExpirationTime())
                .isExpired(jwtService.isTokenExpired(token))
                .build();
    }
}