package com.pe.laboratorio.roles.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pe.laboratorio.roles.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

        Optional<Role> findByName(String name);

        List<Role> findByActiveTrue();

        boolean existsByName(String name);

        @Query("SELECT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.id = :id")
        Optional<Role> findByIdWithPermissions(Long id);

        @Query("SELECT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.name = :name")
        Optional<Role> findByNameWithPermissions(String name);
}
