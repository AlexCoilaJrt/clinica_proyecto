package com.pe.laboratorio.roles.service;

import java.util.List;

import com.pe.laboratorio.roles.dto.request.RoleRequest;
import com.pe.laboratorio.roles.dto.response.RoleResponse;

public interface RoleService {

    RoleResponse createRole(RoleRequest request);

    RoleResponse updateRole(Long id, RoleRequest request);

    RoleResponse getRoleById(Long id);

    RoleResponse getRoleByName(String name);

    List<RoleResponse> getAllRoles();

    List<RoleResponse> getActiveRoles();

    void deleteRole(Long id);

    RoleResponse assignPermissions(Long roleId, List<Long> permissionIds);

    RoleResponse removePermissions(Long roleId, List<Long> permissionIds);
}