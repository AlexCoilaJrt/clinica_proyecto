package com.pe.laboratorio.subexam.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pe.laboratorio.subexam.entity.SubExam;

@Repository
public interface SubExamRepository extends JpaRepository<SubExam, Long> {

    Optional<SubExam> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    @Query("SELECT s FROM SubExam s WHERE s.examen.id = :examenId AND s.active = true ORDER BY s.ordenVisualizacion")
    List<SubExam> findByExamenIdOrderByOrden(@Param("examenId") Long examenId);

    List<SubExam> findByExamenIdAndActiveTrue(Long examenId);

    @Query("SELECT COUNT(s) FROM SubExam s WHERE s.examen.id = :examenId AND s.active = true")
    Long countByExamenId(@Param("examenId") Long examenId);
}