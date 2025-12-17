package com.pe.laboratorio.users.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.laboratorio.exception.ResourceNotFoundException;
import com.pe.laboratorio.exception.ValidationException;
import com.pe.laboratorio.roles.entity.Role;
import com.pe.laboratorio.roles.repository.RoleRepository;
import com.pe.laboratorio.users.dto.CreateUserRequest;
import com.pe.laboratorio.users.dto.UpdateUserRequest;
import com.pe.laboratorio.users.dto.UserResponse;
import com.pe.laboratorio.users.entity.DatosPersonales;
import com.pe.laboratorio.users.repository.DatosPersonalesRepository;
import com.pe.laboratorio.users.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final DatosPersonalesRepository datosPersonalesRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user: {}", request.getUsername());

        // Validar que el login (username) no exista
        if (datosPersonalesRepository.existsByLogin(request.getUsername())) {
            throw new ValidationException("El nombre de usuario '" + request.getUsername() + "' ya existe");
        }

        // Validar que el email no exista
        if (datosPersonalesRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("El email '" + request.getEmail() + "' ya está registrado");
        }

        // Crear usuario (DatosPersonales)
        DatosPersonales datosPersonales = DatosPersonales.builder()
                .login(request.getUsername())
                .email(request.getEmail())
                .passwd(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getFirstName())
                .apepat(request.getLastName())
                .fonLocal(request.getPhone())
                .active(true)
                .roles(new HashSet<>())
                .build();

        // Asignar roles
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = request.getRoleIds().stream()
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + roleId)))
                    .collect(Collectors.toSet());
            datosPersonales.setRoles(roles);
        }

        DatosPersonales savedUser = datosPersonalesRepository.save(datosPersonales);
        log.info("User created successfully: {}", savedUser.getLogin());

        return mapToResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        log.info("Updating user with id: {}", id);

        DatosPersonales datosPersonales = datosPersonalesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        // Validar login (username) si cambió
        if (!datosPersonales.getLogin().equals(request.getUsername()) &&
                datosPersonalesRepository.existsByLogin(request.getUsername())) {
            throw new ValidationException("El nombre de usuario '" + request.getUsername() + "' ya existe");
        }

        // Validar email si cambió
        if (!datosPersonales.getEmail().equals(request.getEmail()) &&
                datosPersonalesRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("El email '" + request.getEmail() + "' ya está registrado");
        }

        // Actualizar campos
        datosPersonales.setLogin(request.getUsername());
        datosPersonales.setEmail(request.getEmail());
        datosPersonales.setNombre(request.getFirstName());
        datosPersonales.setApepat(request.getLastName());
        datosPersonales.setFonLocal(request.getPhone());

        // Actualizar contraseña solo si se proporciona
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            datosPersonales.setPasswd(passwordEncoder.encode(request.getPassword()));
        }

        // Actualizar roles si se proporcionan
        if (request.getRoleIds() != null) {
            Set<Role> roles = request.getRoleIds().stream()
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + roleId)))
                    .collect(Collectors.toSet());
            datosPersonales.setRoles(roles);
        }

        DatosPersonales updatedUser = datosPersonalesRepository.save(datosPersonales);
        log.info("User updated successfully: {}", updatedUser.getLogin());

        return mapToResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Getting user by id: {}", id);

        DatosPersonales datosPersonales = datosPersonalesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        return mapToResponse(datosPersonales);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByLogin(String login) {
        log.info("Getting user by login: {}", login);

        DatosPersonales datosPersonales = datosPersonalesRepository.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con login: " + login));

        return mapToResponse(datosPersonales);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Getting all users");

        return datosPersonalesRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getActiveUsers() {
        log.info("Getting active users");

        return datosPersonalesRepository.findAll().stream()
                .filter(DatosPersonales::getActive)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        DatosPersonales datosPersonales = datosPersonalesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        // Verificar que no sea administrador
        if (datosPersonales.hasRole("ADMINISTRADOR")) {
            throw new ValidationException("No se puede eliminar un usuario con rol ADMINISTRADOR");
        }

        // Soft delete
        datosPersonales.setActive(false);
        datosPersonalesRepository.save(datosPersonales);

        log.info("User deleted (soft delete): {}", datosPersonales.getLogin());
    }

    @Override
    public void toggleUserStatus(Long id, boolean active) {
        log.info("Toggling user status - id: {}, active: {}", id, active);

        DatosPersonales datosPersonales = datosPersonalesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        // Verificar que no sea administrador al desactivar
        if (datosPersonales.hasRole("ADMINISTRADOR") && !active) {
            throw new ValidationException("No se puede desactivar la cuenta de un ADMINISTRADOR");
        }

        datosPersonales.setActive(active);
        datosPersonalesRepository.save(datosPersonales);

        log.info("User status changed: {} - active: {}", datosPersonales.getLogin(), active);
    }

    @Override
    public UserResponse assignRoles(Long userId, List<Long> roleIds) {
        log.info("Assigning {} roles to user: {}", roleIds.size(), userId);

        DatosPersonales datosPersonales = datosPersonalesRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        Set<Role> rolesToAdd = roleIds.stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + roleId)))
                .collect(Collectors.toSet());

        datosPersonales.getRoles().addAll(rolesToAdd);
        DatosPersonales updatedUser = datosPersonalesRepository.save(datosPersonales);

        log.info("Roles assigned successfully to user: {}", datosPersonales.getLogin());

        return mapToResponse(updatedUser);
    }

    @Override
    public UserResponse removeRoles(Long userId, List<Long> roleIds) {
        log.info("Removing {} roles from user: {}", roleIds.size(), userId);

        DatosPersonales datosPersonales = datosPersonalesRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        Set<Role> rolesToRemove = roleIds.stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + roleId)))
                .collect(Collectors.toSet());

        datosPersonales.getRoles().removeAll(rolesToRemove);

        // Validar que no se quede sin roles
        if (datosPersonales.getRoles().isEmpty()) {
            throw new ValidationException("Un usuario debe tener al menos un rol asignado");
        }

        DatosPersonales updatedUser = datosPersonalesRepository.save(datosPersonales);

        log.info("Roles removed successfully from user: {}", datosPersonales.getLogin());

        return mapToResponse(updatedUser);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("Changing password for user: {}", userId);

        DatosPersonales datosPersonales = datosPersonalesRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(oldPassword, datosPersonales.getPasswd())) {
            throw new ValidationException("La contraseña actual es incorrecta");
        }

        // Validar que la nueva contraseña sea diferente
        if (passwordEncoder.matches(newPassword, datosPersonales.getPasswd())) {
            throw new ValidationException("La nueva contraseña debe ser diferente a la actual");
        }

        // Cambiar contraseña
        datosPersonales.setPasswd(passwordEncoder.encode(newPassword));
        datosPersonalesRepository.save(datosPersonales);

        log.info("Password changed successfully for user: {}", datosPersonales.getLogin());
    }

    // ========================================
    // Helper Methods
    // ========================================

    private UserResponse mapToResponse(DatosPersonales datosPersonales) {
        Set<String> roleNames = datosPersonales.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        Set<String> permissions = datosPersonales.getPermissionNames();

        return UserResponse.builder()
                .id(datosPersonales.getId())
                .username(datosPersonales.getLogin())
                .email(datosPersonales.getEmail())
                .firstName(datosPersonales.getNombre())
                .lastName(datosPersonales.getApepat())
                .phone(datosPersonales.getFonLocal())
                .active(datosPersonales.getActive())
                .roles(roleNames)
                .permissions(permissions)
                .createdAt(datosPersonales.getCreatedAt())
                .updatedAt(datosPersonales.getUpdatedAt())
                .lastLogin(datosPersonales.getLastLogin())
                .build();
    }
}