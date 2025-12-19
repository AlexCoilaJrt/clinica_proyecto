package com.pe.laboratorio.examtype.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pe.laboratorio.examtype.entity.ExamType;

@Repository
public interface ExamTypeRepository extends JpaRepository<ExamType, Long> {

    Optional<ExamType> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<ExamType> findByActiveTrue();
}