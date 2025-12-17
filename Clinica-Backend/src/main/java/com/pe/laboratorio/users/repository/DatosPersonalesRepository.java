package com.pe.laboratorio.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pe.laboratorio.users.entity.DatosPersonales;

@Repository
public interface DatosPersonalesRepository extends JpaRepository<DatosPersonales, Long> {

        /**
         * Buscar usuario por login (username)
         */
        Optional<DatosPersonales> findByLogin(String login);

        /**
         * Buscar usuario por email
         */
        Optional<DatosPersonales> findByEmail(String email);

        /**
         * Buscar usuario por id_personal
         */
        Optional<DatosPersonales> findByIdPersonal(Long idPersonal);

        /**
         * Buscar usuario por numdoc (número de documento)
         */
        Optional<DatosPersonales> findByNumdoc(String numdoc);

        /**
         * Verificar si existe un usuario con el login dado
         */
        boolean existsByLogin(String login);

        /**
         * Verificar si existe un usuario con el email dado
         */
        boolean existsByEmail(String email);

        /**
         * Verificar si existe un usuario con el id_personal dado
         */
        boolean existsByIdPersonal(Long idPersonal);

        /**
         * Verificar si existe un usuario con el número de documento dado
         */
        boolean existsByNumdoc(String numdoc);

        /**
         * Buscar usuario por login o email
         */
        @Query("SELECT d FROM DatosPersonales d WHERE d.login = :loginOrEmail OR d.email = :loginOrEmail")
        Optional<DatosPersonales> findByLoginOrEmail(@Param("loginOrEmail") String loginOrEmail);

        /**
         * Buscar usuario activo por login
         */
        @Query("SELECT d FROM DatosPersonales d WHERE d.login = :login AND d.active = true")
        Optional<DatosPersonales> findActiveByLogin(@Param("login") String login);

        /**
         * Buscar usuario activo por email
         */
        @Query("SELECT d FROM DatosPersonales d WHERE d.email = :email AND d.active = true")
        Optional<DatosPersonales> findActiveByEmail(@Param("email") String email);

        /**
         * Buscar usuario por login con roles y permisos cargados (EAGER)
         * Esto evita el problema de LazyInitializationException
         */
        @Query("SELECT DISTINCT d FROM DatosPersonales d " +
                        "LEFT JOIN FETCH d.roles r " +
                        "LEFT JOIN FETCH r.permissions " +
                        "WHERE d.login = :login")
        Optional<DatosPersonales> findByLoginWithRoles(@Param("login") String login);

        /**
         * Buscar usuario por email con roles y permisos cargados (EAGER)
         */
        @Query("SELECT DISTINCT d FROM DatosPersonales d " +
                        "LEFT JOIN FETCH d.roles r " +
                        "LEFT JOIN FETCH r.permissions " +
                        "WHERE d.email = :email")
        Optional<DatosPersonales> findByEmailWithRoles(@Param("email") String email);

        /**
         * Buscar usuario por id_personal con roles y permisos cargados (EAGER)
         */
        @Query("SELECT DISTINCT d FROM DatosPersonales d " +
                        "LEFT JOIN FETCH d.roles r " +
                        "LEFT JOIN FETCH r.permissions " +
                        "WHERE d.idPersonal = :idPersonal")
        Optional<DatosPersonales> findByIdPersonalWithRoles(@Param("idPersonal") Long idPersonal);

        /**
         * Buscar todos los usuarios activos
         */
        @Query("SELECT d FROM DatosPersonales d WHERE d.active = true")
        Iterable<DatosPersonales> findAllActive();

        /**
         * Buscar usuarios por nombre (búsqueda parcial)
         */
        @Query("SELECT d FROM DatosPersonales d WHERE " +
                        "LOWER(d.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                        "LOWER(d.apepat) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                        "LOWER(d.apemat) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
        Iterable<DatosPersonales> searchByName(@Param("searchTerm") String searchTerm);
}