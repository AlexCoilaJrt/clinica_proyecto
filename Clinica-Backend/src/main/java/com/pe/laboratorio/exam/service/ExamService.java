package com.pe.laboratorio.exam.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pe.laboratorio.exam.dto.ExamRequest;
import com.pe.laboratorio.exam.dto.ExamResponse;

public interface ExamService {

    ExamResponse create(ExamRequest request);

    ExamResponse update(Long id, ExamRequest request);

    ExamResponse getById(Long id);

    Page<ExamResponse> getAll(Pageable pageable);

    Page<ExamResponse> getByAreaId(Long areaId, Pageable pageable);

    Page<ExamResponse> searchByNombreOrCodigo(String search, Pageable pageable);

    Page<ExamResponse> getByTipoExamen(Long tipoExamenId, Pageable pageable);

    List<ExamResponse> getPerfiles();

    List<ExamResponse> getAllActive();

    void delete(Long id);

    void toggleStatus(Long id, boolean active);
}