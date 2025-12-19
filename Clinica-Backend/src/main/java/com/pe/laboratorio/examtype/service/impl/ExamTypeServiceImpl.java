package com.pe.laboratorio.examtype.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.laboratorio.examtype.dto.ExamTypeRequest;
import com.pe.laboratorio.examtype.dto.ExamTypeResponse;
import com.pe.laboratorio.examtype.entity.ExamType;
import com.pe.laboratorio.examtype.repository.ExamTypeRepository;
import com.pe.laboratorio.examtype.service.ExamTypeService;
import com.pe.laboratorio.exception.ResourceNotFoundException;
import com.pe.laboratorio.exception.ValidationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExamTypeServiceImpl implements ExamTypeService {

    private final ExamTypeRepository examTypeRepository;

    @Override
    public ExamTypeResponse create(ExamTypeRequest request) {
        log.info("Creating exam type: {}", request.getNombre());

        if (examTypeRepository.existsByNombre(request.getNombre())) {
            throw new ValidationException("Ya existe un tipo de examen con el nombre: " + request.getNombre());
        }

        ExamType examType = ExamType.builder()
                .nombre(request.getNombre().toUpperCase())
                .descripcion(request.getDescripcion())
                .active(true)
                .build();

        ExamType saved = examTypeRepository.save(examType);
        log.info("Exam type created successfully with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    @Override
    public ExamTypeResponse update(Long id, ExamTypeRequest request) {
        log.info("Updating exam type with ID: {}", id);

        ExamType examType = examTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de examen no encontrado con ID: " + id));

        examTypeRepository.findByNombre(request.getNombre()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ValidationException("El nombre ya está en uso por otro tipo de examen");
            }
        });

        examType.setNombre(request.getNombre().toUpperCase());
        examType.setDescripcion(request.getDescripcion());

        ExamType updated = examTypeRepository.save(examType);
        log.info("Exam type updated successfully: {}", updated.getId());

        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamTypeResponse getById(Long id) {
        log.info("Getting exam type by ID: {}", id);

        ExamType examType = examTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de examen no encontrado con ID: " + id));

        return mapToResponse(examType);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamTypeResponse getByNombre(String nombre) {
        log.info("Getting exam type by nombre: {}", nombre);

        ExamType examType = examTypeRepository.findByNombre(nombre)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de examen no encontrado con nombre: " + nombre));

        return mapToResponse(examType);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExamTypeResponse> getAll(Pageable pageable) {
        log.info("Getting all exam types - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        return examTypeRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamTypeResponse> getAllActive() {
        log.info("Getting all active exam types");

        return examTypeRepository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting exam type with ID: {}", id);

        ExamType examType = examTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de examen no encontrado con ID: " + id));

        examType.setActive(false);
        examTypeRepository.save(examType);

        log.info("Exam type deleted (soft delete): {}", id);
    }

    @Override
    public void toggleStatus(Long id, boolean active) {
        log.info("Toggling status for exam type ID: {} to {}", id, active);

        ExamType examType = examTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de examen no encontrado con ID: " + id));

        examType.setActive(active);
        examTypeRepository.save(examType);

        log.info("Exam type status changed: {} - active: {}", id, active);
    }

    private ExamTypeResponse mapToResponse(ExamType examType) {
        return ExamTypeResponse.builder()
                .id(examType.getId())
                .nombre(examType.getNombre())
                .descripcion(examType.getDescripcion())
                .active(examType.getActive())
                .createdAt(examType.getCreatedAt())
                .updatedAt(examType.getUpdatedAt())
                .build();
    }
}