package com.pe.laboratorio.auth.service.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;
    private String type; // "Bearer"
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String sexo;
    private Set<String> roles;
    private Set<String> permissions;
}