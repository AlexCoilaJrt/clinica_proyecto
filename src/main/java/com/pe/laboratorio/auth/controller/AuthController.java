package com.pe.laboratorio.auth.controller;

import com.pe.laboratorio.auth.dto.LoginDTO;
import com.pe.laboratorio.auth.dto.AuthResponse;
import com.pe.laboratorio.auth.dto.RegisterDTO;
import com.pe.laboratorio.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDTO loginDto) {

        AuthResponse response = authService.login(loginDto);
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