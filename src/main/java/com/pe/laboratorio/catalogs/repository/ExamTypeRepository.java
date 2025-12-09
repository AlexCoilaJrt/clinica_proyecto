package com.pe.laboratorio.catalogs.repository;

import com.pe.laboratorio.catalogs.entity.ExamType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamTypeRepository extends JpaRepository<ExamType, Long> {
    Optional<ExamType> findByName(String name);
}