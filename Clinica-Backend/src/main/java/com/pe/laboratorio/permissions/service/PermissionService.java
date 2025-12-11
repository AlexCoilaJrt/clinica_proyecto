package com.pe.laboratorio.permissions.service;

import java.util.List;

import com.pe.laboratorio.permissions.dto.PermissionResponse;

public interface PermissionService {

    PermissionResponse getPermissionById(Long id);

    PermissionResponse getPermissionByName(String name);

    List<PermissionResponse> getAllPermissions();

    List<PermissionResponse> getPermissionsByModule(String module);

    List<PermissionResponse> getActivePermissions();
}