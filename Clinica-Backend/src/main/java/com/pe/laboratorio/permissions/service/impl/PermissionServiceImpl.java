package com.pe.laboratorio.permissions.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.laboratorio.exception.ResourceNotFoundException;
import com.pe.laboratorio.permissions.dto.PermissionResponse;
import com.pe.laboratorio.permissions.entity.Permission;
import com.pe.laboratorio.permissions.repository.PermissionRepository;
import com.pe.laboratorio.permissions.service.PermissionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public PermissionResponse getPermissionById(Long id) {
        log.info("Getting permission by id: {}", id);

        // 1. Ejecutar la búsqueda, que devuelve un Optional<Permission>
        Optional<Permission> permissionOptional = permissionRepository.findById(id);

        // 2. Verificar si el Optional contiene un valor
        if (permissionOptional.isPresent()) {
            // Si existe, obtener el objeto y mapearlo
            Permission permission = permissionOptional.get();
            return mapToResponse(permission);
        } else {
            // Si no existe, lanzar la excepción
            throw new ResourceNotFoundException("Permiso no encontrado con id: " + id);
        }
    }

    @Override
    public PermissionResponse getPermissionByName(String name) {
        log.info("Getting permission by name: {}", name);

        Optional<Permission> permissionOptional = permissionRepository.findByName(name);

        if (permissionOptional.isPresent()) {
            Permission permission = permissionOptional.get();
            return mapToResponse(permission);
        } else {
            throw new ResourceNotFoundException("Permiso no encontrado con nombre: " + name);
        }
    }

    @Override
    public List<PermissionResponse> getAllPermissions() {
        log.info("Getting all permissions");

        List<Permission> permissions = permissionRepository.findAll();

        return permissions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionResponse> getPermissionsByModule(String module) {
        log.info("Getting permissions by module: {}", module);

        List<Permission> permissions = permissionRepository.findByModule(module);

        return permissions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionResponse> getActivePermissions() {
        log.info("Getting active permissions");

        List<Permission> permissions = permissionRepository.findByActiveTrue();

        return permissions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Mapea una entidad Permission a PermissionResponse
     */
    private PermissionResponse mapToResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .module(permission.getModule())
                .active(permission.getActive())
                .createdAt(permission.getCreatedAt())
                .build();
    }
}