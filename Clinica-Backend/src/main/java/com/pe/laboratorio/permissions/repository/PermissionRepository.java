package com.pe.laboratorio.permissions.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pe.laboratorio.permissions.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(String name);

    List<Permission> findByActiveTrue();

    List<Permission> findByModule(String module);

    Set<Permission> findByIdIn(Set<Long> ids);

    boolean existsByName(String name);
}