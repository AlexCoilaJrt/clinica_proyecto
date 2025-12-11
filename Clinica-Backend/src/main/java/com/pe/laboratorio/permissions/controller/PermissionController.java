package com.pe.laboratorio.permissions.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.laboratorio.permissions.dto.PermissionResponse;
import com.pe.laboratorio.permissions.service.PermissionService;
import com.pe.laboratorio.shared.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<ApiResponse<PermissionResponse>> getPermissionById(@PathVariable Long id) {
        PermissionResponse permission = permissionService.getPermissionById(id);
        return ResponseEntity.ok(ApiResponse.<PermissionResponse>builder()
                .success(true)
                .data(permission)
                .build());
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<ApiResponse<PermissionResponse>> getPermissionByName(@PathVariable String name) {
        PermissionResponse permission = permissionService.getPermissionByName(name);
        return ResponseEntity.ok(ApiResponse.<PermissionResponse>builder()
                .success(true)
                .data(permission)
                .build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getAllPermissions() {
        List<PermissionResponse> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(ApiResponse.<List<PermissionResponse>>builder()
                .success(true)
                .data(permissions)
                .build());
    }

    @GetMapping("/module/{module}")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getPermissionsByModule(
            @PathVariable String module) {
        List<PermissionResponse> permissions = permissionService.getPermissionsByModule(module);
        return ResponseEntity.ok(ApiResponse.<List<PermissionResponse>>builder()
                .success(true)
                .data(permissions)
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getActivePermissions() {
        List<PermissionResponse> permissions = permissionService.getActivePermissions();
        return ResponseEntity.ok(ApiResponse.<List<PermissionResponse>>builder()
                .success(true)
                .data(permissions)
                .build());
    }
}