package com.pe.laboratorio.auth.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenInfoResponse {
    private Long timeRemainingSeconds; // Tiempo restante en segundos
    private Long timeRemainingMs; // Tiempo restante en milisegundos
    private Long expirationTimeMs; // Tiempo total de expiración configurado
    private Boolean isExpired; // Si el token ya expiró
}