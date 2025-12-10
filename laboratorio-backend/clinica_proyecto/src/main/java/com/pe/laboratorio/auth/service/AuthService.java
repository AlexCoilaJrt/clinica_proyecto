package com.pe.laboratorio.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pe.laboratorio.auth.service.dto.AuthResponse;
import com.pe.laboratorio.auth.service.dto.LoginDTO;
import com.pe.laboratorio.auth.service.dto.RegisterDTO;
import com.pe.laboratorio.exception.AuthException;
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

    public AuthResponse login(LoginDTO loginDto) {

        var userOptional = userRepository.findByUsername(loginDto.getUsername());

        if (userOptional.isEmpty()) {
            throw new AuthException("Credenciales inválidas (usuario o contraseña incorrectos).");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new AuthException("Credenciales inválidas...");
        }

        if (!user.isEnabled()) {
            throw new AuthException("La cuenta ha sido bloqueada. Contacte al administrador.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        } catch (AuthenticationException e) {
            throw new AuthException("Fallo interno de autenticación.");
        }

        String token = jwtService.generateToken(user.getUsername());
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