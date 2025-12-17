package com.pe.laboratorio.roles.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.laboratorio.exception.ResourceNotFoundException;
import com.pe.laboratorio.exception.ValidationException;
import com.pe.laboratorio.permissions.dto.PermissionResponse;
import com.pe.laboratorio.permissions.entity.Permission;
import com.pe.laboratorio.permissions.repository.PermissionRepository;
import com.pe.laboratorio.roles.dto.request.RoleRequest;
import com.pe.laboratorio.roles.dto.response.RoleResponse;
import com.pe.laboratorio.roles.entity.Role;
import com.pe.laboratorio.roles.repository.RoleRepository;
import com.pe.laboratorio.roles.service.RoleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public RoleResponse createRole(RoleRequest request) {
        log.info("Creating role: {}", request.getName());

        if (roleRepository.existsByName(request.getName().toUpperCase())) {
            throw new ValidationException("El rol con nombre '" + request.getName() + "' ya existe");
        }

        Role role = Role.builder()
                .name(request.getName().toUpperCase())
                .description(request.getDescription())
                .active(request.getActive())
                .permissions(new HashSet<>())
                .build();

        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = permissionRepository.findByIdIn(request.getPermissionIds());
            role.setPermissions(permissions);
        }

        Role savedRole = roleRepository.save(role);
        log.info("Role created successfully: {}", savedRole.getName());
        return mapToResponse(savedRole);
    }

    @Override
    public RoleResponse updateRole(Long id, RoleRequest request) {
        log.info("Updating role with id: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));

        if (!role.getName().equals(request.getName().toUpperCase()) &&
                roleRepository.existsByName(request.getName().toUpperCase())) {
            throw new ValidationException("El rol con nombre '" + request.getName() + "' ya existe");
        }

        role.setName(request.getName().toUpperCase());
        role.setDescription(request.getDescription());
        role.setActive(request.getActive());

        if (request.getPermissionIds() != null) {
            Set<Permission> permissions = permissionRepository.findByIdIn(request.getPermissionIds());
            role.setPermissions(permissions);
        }

        Role updatedRole = roleRepository.save(role);
        log.info("Role updated successfully: {}", updatedRole.getName());
        return mapToResponse(updatedRole);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        log.info("Getting role by id: {}", id);
        Role role = roleRepository.findByIdWithPermissions(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));
        return mapToResponse(role);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleByName(String name) {
        log.info("Getting role by name: {}", name);
        Role role = roleRepository.findByNameWithPermissions(name.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con nombre: " + name));
        return mapToResponse(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        log.info("Getting all roles");
        return roleRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getActiveRoles() {
        log.info("Getting active roles");
        return roleRepository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRole(Long id) {
        log.info("Deleting role with id: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));

        // Soft delete
        role.setActive(false);
        roleRepository.save(role);
        log.info("Role deleted (soft delete): {}", role.getName());
    }

    @Override
    public RoleResponse assignPermissions(Long roleId, List<Long> permissionIds) {
        log.info("Assigning {} permissions to role: {}", permissionIds.size(), roleId);

        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + roleId));

        Set<Permission> permissions = permissionRepository.findByIdIn(new HashSet<>(permissionIds));

        if (permissions.isEmpty()) {
            throw new ValidationException("No se encontraron permisos con los IDs proporcionados");
        }

        role.getPermissions().addAll(permissions);

        Role updatedRole = roleRepository.save(role);
        log.info("Permissions assigned successfully to role: {}", updatedRole.getName());
        return mapToResponse(updatedRole);
    }

    @Override
    public RoleResponse removePermissions(Long roleId, List<Long> permissionIds) {
        log.info("Removing {} permissions from role: {}", permissionIds.size(), roleId);

        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + roleId));

        Set<Permission> permissionsToRemove = permissionRepository.findByIdIn(new HashSet<>(permissionIds));
        role.getPermissions().removeAll(permissionsToRemove);

        Role updatedRole = roleRepository.save(role);
        log.info("Permissions removed successfully from role: {}", updatedRole.getName());
        return mapToResponse(updatedRole);
    }

    private RoleResponse mapToResponse(Role role) {
        Set<PermissionResponse> permissionResponses = role.getPermissions().stream()
                .map(permission -> PermissionResponse.builder()
                        .id(permission.getId())
                        .name(permission.getName())
                        .description(permission.getDescription())
                        .module(permission.getModule())
                        .active(permission.getActive())
                        .createdAt(permission.getCreatedAt())
                        .build())
                .collect(Collectors.toSet());

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .active(role.getActive())
                .permissions(permissionResponses)
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}