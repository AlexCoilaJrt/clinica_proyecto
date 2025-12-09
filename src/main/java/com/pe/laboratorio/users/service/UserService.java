package com.pe.laboratorio.users.service;

import com.pe.laboratorio.users.dto.UserRequest;
import com.pe.laboratorio.users.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UserRequest request);

    List<UserResponse> getAllUsers();

    void toggleUserStatus(Long id, boolean enabled);
}