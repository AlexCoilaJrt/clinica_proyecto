package com.pe.laboratorio.users.service.impl;

import com.pe.laboratorio.users.dto.UserRequest;
import com.pe.laboratorio.users.dto.UserResponse;
import com.pe.laboratorio.users.entity.User;
import com.pe.laboratorio.users.repository.UserRepository;
import com.pe.laboratorio.users.service.UserService;
import com.pe.laboratorio.exception.AuthException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AuthException("El nombre de usuario ya existe.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);

        return new UserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getRole());
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthException("Usuario no encontrado con ID: " + id));

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setUsername(request.getUsername());
        user.setRole(request.getRole());

        User updatedUser = userRepository.save(user);

        return new UserResponse(updatedUser.getId(), updatedUser.getUsername(), updatedUser.getRole());
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(user.getId(), user.getUsername(), user.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthException("Usuario no encontrado con ID: " + id));

        return new UserResponse(user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public void toggleUserStatus(Long id, boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthException("Usuario no encontrado con ID: " + id));

        if (user.getRole().equals("ROLE_ADMIN") && !enabled) {
            throw new AuthException("No se puede desactivar la cuenta del Administrador principal.");
        }

        user.setEnabled(enabled);
        userRepository.save(user);
    }
}