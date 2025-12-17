package com.pe.laboratorio.roles.controller;

import com.pe.laboratorio.shared.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.pe.laboratorio.roles.dto.request.RoleRequest;
import com.pe.laboratorio.roles.dto.response.RoleResponse;
import com.pe.laboratorio.roles.service.RoleService;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody RoleRequest request) {
        RoleResponse role = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<RoleResponse>builder()
                        .success(true)
                        .message("Rol creado exitosamente")
                        .data(role)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequest request) {
        RoleResponse role = roleService.updateRole(id, request);
        return ResponseEntity.ok(ApiResponse.<RoleResponse>builder()
                .success(true)
                .message("Rol actualizado exitosamente")
                .data(role)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable Long id) {
        RoleResponse role = roleService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.<RoleResponse>builder()
                .success(true)
                .data(role)
                .build());
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleByName(@PathVariable String name) {
        RoleResponse role = roleService.getRoleByName(name);
        return ResponseEntity.ok(ApiResponse.<RoleResponse>builder()
                .success(true)
                .data(role)
                .build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.<List<RoleResponse>>builder()
                .success(true)
                .data(roles)
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getActiveRoles() {
        List<RoleResponse> roles = roleService.getActiveRoles();
        return ResponseEntity.ok(ApiResponse.<List<RoleResponse>>builder()
                .success(true)
                .data(roles)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Rol eliminado exitosamente")
                .build());
    }

    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<ApiResponse<RoleResponse>> assignPermissions(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds) {
        RoleResponse role = roleService.assignPermissions(roleId, permissionIds);
        return ResponseEntity.ok(ApiResponse.<RoleResponse>builder()
                .success(true)
                .message("Permisos asignados exitosamente")
                .data(role)
                .build());
    }

    @DeleteMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<ApiResponse<RoleResponse>> removePermissions(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds) {
        RoleResponse role = roleService.removePermissions(roleId, permissionIds);
        return ResponseEntity.ok(ApiResponse.<RoleResponse>builder()
                .success(true)
                .message("Permisos removidos exitosamente")
                .data(role)
                .build());
    }
}