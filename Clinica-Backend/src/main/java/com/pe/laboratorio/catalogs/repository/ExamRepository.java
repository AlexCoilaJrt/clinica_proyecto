package com.pe.laboratorio.catalogs.repository;

import com.pe.laboratorio.catalogs.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    Optional<Exam> findByName(String name);
}