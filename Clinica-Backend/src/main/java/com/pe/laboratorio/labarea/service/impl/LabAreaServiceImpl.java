package com.pe.laboratorio.labarea.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.laboratorio.exception.ResourceNotFoundException;
import com.pe.laboratorio.exception.ValidationException;
import com.pe.laboratorio.labarea.dto.LabAreaRequest;
import com.pe.laboratorio.labarea.dto.LabAreaResponse;
import com.pe.laboratorio.labarea.entity.LabArea;
import com.pe.laboratorio.labarea.repository.LabAreaRepository;
import com.pe.laboratorio.labarea.service.LabAreaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LabAreaServiceImpl implements LabAreaService {

    private final LabAreaRepository labAreaRepository;

    @Override
    public LabAreaResponse create(LabAreaRequest request) {
        log.info("Creating lab area with code: {}", request.getCodigo());

        // Validar que el código no exista
        if (labAreaRepository.existsByCodigo(request.getCodigo())) {
            throw new ValidationException("Ya existe un área con el código: " + request.getCodigo());
        }

        LabArea labArea = LabArea.builder()
                .codigo(request.getCodigo().toUpperCase())
                .descripcion(request.getDescripcion().toUpperCase())
                .active(true)
                .build();

        LabArea saved = labAreaRepository.save(labArea);
        log.info("Lab area created successfully with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    @Override
    public LabAreaResponse update(Long id, LabAreaRequest request) {
        log.info("Updating lab area with ID: {}", id);

        LabArea labArea = labAreaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Área de laboratorio no encontrada con ID: " + id));

        // Validar que el código no esté en uso por otra área
        if (labAreaRepository.existsByCodigoAndIdNot(request.getCodigo(), id)) {
            throw new ValidationException("El código ya está en uso por otra área");
        }

        labArea.setCodigo(request.getCodigo().toUpperCase());
        labArea.setDescripcion(request.getDescripcion().toUpperCase());

        LabArea updated = labAreaRepository.save(labArea);
        log.info("Lab area updated successfully: {}", updated.getId());

        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public LabAreaResponse getById(Long id) {
        log.info("Getting lab area by ID: {}", id);

        LabArea labArea = labAreaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Área de laboratorio no encontrada con ID: " + id));

        return mapToResponse(labArea);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LabAreaResponse> getAll(Pageable pageable) {
        log.info("Getting all lab areas - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        return labAreaRepository.findByActiveTrue(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LabAreaResponse> searchByDescripcion(String descripcion, Pageable pageable) {
        log.info("Searching lab areas by description: {}", descripcion);

        return labAreaRepository.findByDescripcionContainingIgnoreCaseAndActiveTrue(descripcion, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabAreaResponse> getAllActive() {
        log.info("Getting all active lab areas");

        return labAreaRepository.findByActiveTrue(Pageable.unpaged())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting lab area with ID: {}", id);

        LabArea labArea = labAreaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Área de laboratorio no encontrada con ID: " + id));

        // Soft delete
        labArea.setActive(false);
        labAreaRepository.save(labArea);

        log.info("Lab area deleted (soft delete): {}", id);
    }

    @Override
    public void toggleStatus(Long id, boolean active) {
        log.info("Toggling status for lab area ID: {} to {}", id, active);

        LabArea labArea = labAreaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Área de laboratorio no encontrada con ID: " + id));

        labArea.setActive(active);
        labAreaRepository.save(labArea);

        log.info("Lab area status changed: {} - active: {}", id, active);
    }

    // ========================================
    // Helper Methods
    // ========================================

    private LabAreaResponse mapToResponse(LabArea labArea) {
        return LabAreaResponse.builder()
                .id(labArea.getId())
                .codigo(labArea.getCodigo())
                .descripcion(labArea.getDescripcion())
                .active(labArea.getActive())
                .createdAt(labArea.getCreatedAt())
                .updatedAt(labArea.getUpdatedAt())
                .build();
    }
}