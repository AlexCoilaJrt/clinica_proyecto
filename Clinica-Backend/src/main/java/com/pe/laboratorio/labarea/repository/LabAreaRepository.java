package com.pe.laboratorio.labarea.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pe.laboratorio.labarea.entity.LabArea;

@Repository
public interface LabAreaRepository extends JpaRepository<LabArea, Long> {

    Optional<LabArea> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    Page<LabArea> findByActiveTrue(Pageable pageable);

    Page<LabArea> findByDescripcionContainingIgnoreCaseAndActiveTrue(String descripcion, Pageable pageable);
}