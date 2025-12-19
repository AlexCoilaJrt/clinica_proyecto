package com.pe.laboratorio.exam.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pe.laboratorio.exam.entity.Exam;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    Optional<Exam> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    Page<Exam> findByActiveTrue(Pageable pageable);

    Page<Exam> findByAreaIdAndActiveTrue(Long areaId, Pageable pageable);

    @Query("SELECT e FROM Exam e WHERE e.area.id = :areaId AND e.active = true")
    List<Exam> findByAreaId(@Param("areaId") Long areaId);

    @Query("SELECT e FROM Exam e WHERE " +
            "(LOWER(e.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.codigo) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "e.active = true")
    Page<Exam> searchByNombreOrCodigo(@Param("search") String search, Pageable pageable);

    @Query("SELECT e FROM Exam e WHERE e.tipoExamen.id = :tipoExamenId AND e.active = true")
    Page<Exam> findByTipoExamenId(@Param("tipoExamenId") Long tipoExamenId, Pageable pageable);

    List<Exam> findByEsPerfilTrueAndActiveTrue();
}