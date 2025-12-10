package com.pe.laboratorio.auth.controller;

import com.pe.laboratorio.auth.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pe.laboratorio.auth.service.dto.AuthResponse;
import com.pe.laboratorio.auth.service.dto.LoginDTO;
import com.pe.laboratorio.auth.service.dto.RegisterDTO;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginDTO loginDto,
            HttpServletRequest request) {

        AuthResponse response = authService.login(loginDto, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test/protegida")
    public ResponseEntity<String> rutaProtegida() {
        return ResponseEntity.ok("Acceso Exitoso! Bienvenido al Área de Pacientes.");
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterDTO registerDto) {
        AuthResponse response = authService.register(registerDto);
        return ResponseEntity.ok(response);
    }
}