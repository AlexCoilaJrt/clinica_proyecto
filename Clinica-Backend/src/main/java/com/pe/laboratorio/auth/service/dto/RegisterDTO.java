package com.pe.laboratorio.auth.service.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username;
    private String password;
    private String role;
}