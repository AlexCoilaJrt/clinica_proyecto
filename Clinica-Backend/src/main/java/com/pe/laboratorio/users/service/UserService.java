package com.pe.laboratorio.users.service;

import java.util.List;

import com.pe.laboratorio.users.dto.CreateUserRequest;
import com.pe.laboratorio.users.dto.UpdateUserRequest;
import com.pe.laboratorio.users.dto.UserResponse;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    UserResponse getUserById(Long id);

    UserResponse getUserByLogin(String login);

    List<UserResponse> getAllUsers();

    List<UserResponse> getActiveUsers();

    void deleteUser(Long id);

    void toggleUserStatus(Long id, boolean active);

    UserResponse assignRoles(Long userId, List<Long> roleIds);

    UserResponse removeRoles(Long userId, List<Long> roleIds);

    void changePassword(Long userId, String oldPassword, String newPassword);
}