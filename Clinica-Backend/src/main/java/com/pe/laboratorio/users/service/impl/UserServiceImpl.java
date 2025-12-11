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
import com.pe.laboratorio.users.entity.User;
import com.pe.laboratorio.users.repository.UserRepository;
import com.pe.laboratorio.users.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user: {}", request.getUsername());

        // Validar que el username no exista
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ValidationException("El nombre de usuario '" + request.getUsername() + "' ya existe");
        }

        // Validar que el email no exista
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("El email '" + request.getEmail() + "' ya está registrado");
        }

        // Crear usuario
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .active(true)
                .roles(new HashSet<>())
                .build();

        // Asignar roles
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = request.getRoleIds().stream()
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + roleId)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        User savedUser = userRepository.save(user);
        log.info("User created successfully: {}", savedUser.getUsername());

        return mapToResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        log.info("Updating user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        // Validar username si cambió
        if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new ValidationException("El nombre de usuario '" + request.getUsername() + "' ya existe");
        }

        // Validar email si cambió
        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("El email '" + request.getEmail() + "' ya está registrado");
        }

        // Actualizar campos
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());

        // Actualizar contraseña solo si se proporciona
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Actualizar roles si se proporcionan
        if (request.getRoleIds() != null) {
            Set<Role> roles = request.getRoleIds().stream()
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + roleId)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", updatedUser.getUsername());

        return mapToResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Getting user by id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        log.info("Getting user by username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con username: " + username));

        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Getting all users");

        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getActiveUsers() {
        log.info("Getting active users");

        return userRepository.findAll().stream()
                .filter(User::getActive)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        // Verificar que no sea administrador
        if (user.hasRole("ADMINISTRADOR")) {
            throw new ValidationException("No se puede eliminar un usuario con rol ADMINISTRADOR");
        }

        // Soft delete
        user.setActive(false);
        userRepository.save(user);

        log.info("User deleted (soft delete): {}", user.getUsername());
    }

    @Override
    public void toggleUserStatus(Long id, boolean active) {
        log.info("Toggling user status - id: {}, active: {}", id, active);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        // Verificar que no sea administrador al desactivar
        if (user.hasRole("ADMINISTRADOR") && !active) {
            throw new ValidationException("No se puede desactivar la cuenta de un ADMINISTRADOR");
        }

        user.setActive(active);
        userRepository.save(user);

        log.info("User status changed: {} - active: {}", user.getUsername(), active);
    }

    @Override
    public UserResponse assignRoles(Long userId, List<Long> roleIds) {
        log.info("Assigning {} roles to user: {}", roleIds.size(), userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        Set<Role> rolesToAdd = roleIds.stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + roleId)))
                .collect(Collectors.toSet());

        user.getRoles().addAll(rolesToAdd);
        User updatedUser = userRepository.save(user);

        log.info("Roles assigned successfully to user: {}", user.getUsername());

        return mapToResponse(updatedUser);
    }

    @Override
    public UserResponse removeRoles(Long userId, List<Long> roleIds) {
        log.info("Removing {} roles from user: {}", roleIds.size(), userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        Set<Role> rolesToRemove = roleIds.stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + roleId)))
                .collect(Collectors.toSet());

        user.getRoles().removeAll(rolesToRemove);

        // Validar que no se quede sin roles
        if (user.getRoles().isEmpty()) {
            throw new ValidationException("Un usuario debe tener al menos un rol asignado");
        }

        User updatedUser = userRepository.save(user);

        log.info("Roles removed successfully from user: {}", user.getUsername());

        return mapToResponse(updatedUser);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("Changing password for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ValidationException("La contraseña actual es incorrecta");
        }

        // Validar que la nueva contraseña sea diferente
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ValidationException("La nueva contraseña debe ser diferente a la actual");
        }

        // Cambiar contraseña
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", user.getUsername());
    }

    // ========================================
    // Helper Methods
    // ========================================

    private UserResponse mapToResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        Set<String> permissions = user.getPermissionNames();

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .active(user.getActive())
                .roles(roleNames)
                .permissions(permissions)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }
}