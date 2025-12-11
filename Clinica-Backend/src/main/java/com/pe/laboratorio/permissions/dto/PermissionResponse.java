package com.pe.laboratorio.permissions.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponse {

    private Long id;
    private String name;
    private String description;
    private String module;
    private Boolean active;
    private LocalDateTime createdAt;
}