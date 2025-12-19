package com.pe.laboratorio.examtype.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pe.laboratorio.examtype.dto.ExamTypeRequest;
import com.pe.laboratorio.examtype.dto.ExamTypeResponse;

public interface ExamTypeService {

    ExamTypeResponse create(ExamTypeRequest request);

    ExamTypeResponse update(Long id, ExamTypeRequest request);

    ExamTypeResponse getById(Long id);

    ExamTypeResponse getByNombre(String nombre);

    Page<ExamTypeResponse> getAll(Pageable pageable);

    List<ExamTypeResponse> getAllActive();

    void delete(Long id);

    void toggleStatus(Long id, boolean active);
}