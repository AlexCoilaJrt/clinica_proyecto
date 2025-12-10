package com.pe.laboratorio.catalogs.repository;

import com.pe.laboratorio.catalogs.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
    Optional<Area> findByName(String name);
}