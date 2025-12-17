package com.pe.laboratorio.users.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pe.laboratorio.shared.dto.ApiResponse;
import com.pe.laboratorio.users.dto.ChangePasswordRequest;
import com.pe.laboratorio.users.dto.CreateUserRequest;
import com.pe.laboratorio.users.dto.UpdateUserRequest;
import com.pe.laboratorio.users.dto.UserResponse;
import com.pe.laboratorio.users.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

        private final UserService userService;

        /**
         * Crear un nuevo usuario
         * POST /api/v1/users
         */
        @PostMapping
        @PreAuthorize("hasAuthority('USER_CREATE')")
        public ResponseEntity<ApiResponse<UserResponse>> createUser(
                        @Valid @RequestBody CreateUserRequest request) {
                UserResponse user = userService.createUser(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.<UserResponse>builder()
                                                .success(true)
                                                .message("Usuario creado exitosamente")
                                                .data(user)
                                                .build());
        }

        /**
         * Actualizar un usuario
         * PUT /api/v1/users/{id}
         */
        @PutMapping("/{id}")
        @PreAuthorize("hasAuthority('USER_UPDATE')")
        public ResponseEntity<ApiResponse<UserResponse>> updateUser(
                        @PathVariable Long id,
                        @Valid @RequestBody UpdateUserRequest request) {
                UserResponse user = userService.updateUser(id, request);
                return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                                .success(true)
                                .message("Usuario actualizado exitosamente")
                                .data(user)
                                .build());
        }

        /**
         * Obtener un usuario por ID
         * GET /api/v1/users/{id}
         */
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('USER_READ')")
        public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
                UserResponse user = userService.getUserById(id);
                return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                                .success(true)
                                .data(user)
                                .build());
        }

        /**
         * Listar todos los usuarios
         * GET /api/v1/users
         */
        @GetMapping
        @PreAuthorize("hasAuthority('USER_READ')")
        public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
                List<UserResponse> users = userService.getAllUsers();
                return ResponseEntity.ok(ApiResponse.<List<UserResponse>>builder()
                                .success(true)
                                .message("Usuarios obtenidos exitosamente")
                                .data(users)
                                .build());
        }

        /**
         * Listar solo usuarios activos
         * GET /api/v1/users/active
         */
        @GetMapping("/active")
        @PreAuthorize("hasAuthority('USER_READ')")
        public ResponseEntity<ApiResponse<List<UserResponse>>> getActiveUsers() {
                List<UserResponse> users = userService.getActiveUsers();
                return ResponseEntity.ok(ApiResponse.<List<UserResponse>>builder()
                                .success(true)
                                .message("Usuarios activos obtenidos exitosamente")
                                .data(users)
                                .build());
        }

        /**
         * Eliminar un usuario (soft delete)
         * DELETE /api/v1/users/{id}
         */
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('USER_DELETE')")
        public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
                userService.deleteUser(id);
                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .success(true)
                                .message("Usuario eliminado exitosamente")
                                .build());
        }

        /**
         * Activar/Desactivar un usuario
         * PATCH /api/v1/users/{id}/status?active=true
         */
        @PatchMapping("/{id}/status")
        @PreAuthorize("hasAuthority('USER_BLOCK')")
        public ResponseEntity<ApiResponse<Void>> toggleUserStatus(
                        @PathVariable Long id,
                        @RequestParam boolean active) {
                userService.toggleUserStatus(id, active);
                String message = active ? "Usuario activado exitosamente" : "Usuario desactivado exitosamente";
                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .success(true)
                                .message(message)
                                .build());
        }

        /**
         * Asignar roles a un usuario
         * POST /api/v1/users/{id}/roles
         * Body: [1, 2, 3] (IDs de roles)
         */
        @PostMapping("/{id}/roles")
        @PreAuthorize("hasAuthority('USER_UPDATE')")
        public ResponseEntity<ApiResponse<UserResponse>> assignRoles(
                        @PathVariable Long id,
                        @RequestBody List<Long> roleIds) {
                UserResponse user = userService.assignRoles(id, roleIds);
                return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                                .success(true)
                                .message("Roles asignados exitosamente")
                                .data(user)
                                .build());
        }

        /**
         * Remover roles de un usuario
         * DELETE /api/v1/users/{id}/roles
         * Body: [1, 2] (IDs de roles a remover)
         */
        @DeleteMapping("/{id}/roles")
        @PreAuthorize("hasAuthority('USER_UPDATE')")
        public ResponseEntity<ApiResponse<UserResponse>> removeRoles(
                        @PathVariable Long id,
                        @RequestBody List<Long> roleIds) {
                UserResponse user = userService.removeRoles(id, roleIds);
                return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                                .success(true)
                                .message("Roles removidos exitosamente")
                                .data(user)
                                .build());
        }

        /**
         * Cambiar contraseña de un usuario
         * POST /api/v1/users/{id}/change-password
         */
        @PostMapping("/{id}/change-password")
        @PreAuthorize("hasAuthority('USER_UPDATE') or #id == authentication.principal.id")
        public ResponseEntity<ApiResponse<Void>> changePassword(
                        @PathVariable Long id,
                        @Valid @RequestBody ChangePasswordRequest request) {

                // Validar que las contraseñas coincidan
                if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                        return ResponseEntity.badRequest()
                                        .body(ApiResponse.<Void>builder()
                                                        .success(false)
                                                        .message("Las contraseñas no coinciden")
                                                        .build());
                }

                userService.changePassword(id, request.getOldPassword(), request.getNewPassword());

                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .success(true)
                                .message("Contraseña cambiada exitosamente")
                                .build());
        }
}