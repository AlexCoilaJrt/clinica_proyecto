package com.pe.laboratorio.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pe.laboratorio.users.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Buscar usuario por username
     */
    Optional<User> findByUsername(String username);

    /**
     * Buscar usuario por email
     */
    Optional<User> findByEmail(String email);

    /**
     * Verificar si existe un usuario con el username dado
     */
    boolean existsByUsername(String username);

    /**
     * Verificar si existe un usuario con el email dado
     */
    boolean existsByEmail(String email);

    /**
     * Buscar usuario por username o email
     */
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    /**
     * Buscar usuario activo por username
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.active = true")
    Optional<User> findActiveByUsername(@Param("username") String username);

    /**
     * Buscar usuario activo por email
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.active = true")
    Optional<User> findActiveByEmail(@Param("email") String email);

    /**
     * Buscar usuario por username con roles y permisos cargados (EAGER)
     * Esto evita el problema de LazyInitializationException
     */
    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.roles r " +
            "LEFT JOIN FETCH r.permissions " +
            "WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    /**
     * Buscar usuario por email con roles y permisos cargados (EAGER)
     */
    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.roles r " +
            "LEFT JOIN FETCH r.permissions " +
            "WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);
}