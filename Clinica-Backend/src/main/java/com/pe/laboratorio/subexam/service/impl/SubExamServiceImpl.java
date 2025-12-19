package com.pe.laboratorio.subexam.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.laboratorio.exam.entity.Exam;
import com.pe.laboratorio.exam.repository.ExamRepository;
import com.pe.laboratorio.exception.ResourceNotFoundException;
import com.pe.laboratorio.exception.ValidationException;
import com.pe.laboratorio.subexam.dto.SubExamRequest;
import com.pe.laboratorio.subexam.dto.SubExamResponse;
import com.pe.laboratorio.subexam.entity.SubExam;
import com.pe.laboratorio.subexam.repository.SubExamRepository;
import com.pe.laboratorio.subexam.service.SubExamService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubExamServiceImpl implements SubExamService {

    private final SubExamRepository subExamRepository;
    private final ExamRepository examRepository;

    @Override
    public SubExamResponse create(SubExamRequest request) {
        log.info("Creating sub-exam with code: {}", request.getCodigo());

        if (subExamRepository.existsByCodigo(request.getCodigo())) {
            throw new ValidationException("Ya existe un subexamen con el código: " + request.getCodigo());
        }

        Exam examen = examRepository.findById(request.getExamenId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Examen no encontrado con ID: " + request.getExamenId()));

        if (!examen.getEsPerfil()) {
            throw new ValidationException("El examen debe ser un perfil para poder agregar subexámenes");
        }

        SubExam subExam = SubExam.builder()
                .examen(examen)
                .codigo(request.getCodigo().toUpperCase())
                .nombre(request.getNombre().toUpperCase())
                .tipoResultado(request.getTipoResultado())
                .unidadMedida(request.getUnidadMedida())
                .valorMinimo(request.getValorMinimo())
                .valorMaximo(request.getValorMaximo())
                .valorCriticoMin(request.getValorCriticoMin())
                .valorCriticoMax(request.getValorCriticoMax())
                .ordenVisualizacion(request.getOrdenVisualizacion())
                .observaciones(request.getObservaciones())
                .active(true)
                .build();

        SubExam saved = subExamRepository.save(subExam);
        log.info("Sub-exam created successfully with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    @Override
    public SubExamResponse update(Long id, SubExamRequest request) {
        log.info("Updating sub-exam with ID: {}", id);

        SubExam subExam = subExamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subexamen no encontrado con ID: " + id));

        if (subExamRepository.existsByCodigoAndIdNot(request.getCodigo(), id)) {
            throw new ValidationException("El código ya está en uso por otro subexamen");
        }

        Exam examen = examRepository.findById(request.getExamenId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Examen no encontrado con ID: " + request.getExamenId()));

        subExam.setExamen(examen);
        subExam.setCodigo(request.getCodigo().toUpperCase());
        subExam.setNombre(request.getNombre().toUpperCase());
        subExam.setTipoResultado(request.getTipoResultado());
        subExam.setUnidadMedida(request.getUnidadMedida());
        subExam.setValorMinimo(request.getValorMinimo());
        subExam.setValorMaximo(request.getValorMaximo());
        subExam.setValorCriticoMin(request.getValorCriticoMin());
        subExam.setValorCriticoMax(request.getValorCriticoMax());
        subExam.setOrdenVisualizacion(request.getOrdenVisualizacion());
        subExam.setObservaciones(request.getObservaciones());

        SubExam updated = subExamRepository.save(subExam);
        log.info("Sub-exam updated successfully: {}", updated.getId());

        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public SubExamResponse getById(Long id) {
        log.info("Getting sub-exam by ID: {}", id);

        SubExam subExam = subExamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subexamen no encontrado con ID: " + id));

        return mapToResponse(subExam);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubExamResponse> getByExamenId(Long examenId) {
        log.info("Getting sub-exams by exam ID: {}", examenId);

        if (!examRepository.existsById(examenId)) {
            throw new ResourceNotFoundException("Examen no encontrado con ID: " + examenId);
        }

        return subExamRepository.findByExamenIdOrderByOrden(examenId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting sub-exam with ID: {}", id);

        SubExam subExam = subExamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subexamen no encontrado con ID: " + id));

        subExam.setActive(false);
        subExamRepository.save(subExam);

        log.info("Sub-exam deleted (soft delete): {}", id);
    }

    @Override
    public void toggleStatus(Long id, boolean active) {
        log.info("Toggling status for sub-exam ID: {} to {}", id, active);

        SubExam subExam = subExamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subexamen no encontrado con ID: " + id));

        subExam.setActive(active);
        subExamRepository.save(subExam);

        log.info("Sub-exam status changed: {} - active: {}", id, active);
    }

    @Override
    public void reorderSubExams(Long examenId, List<Long> subExamIds) {
        log.info("Reordering sub-exams for exam ID: {}", examenId);

        // Validar que el examen exista
        if (!examRepository.existsById(examenId)) {
            throw new ResourceNotFoundException("Examen no encontrado con ID: " + examenId);
        }

        for (int i = 0; i < subExamIds.size(); i++) {
            Long subExamId = subExamIds.get(i);
            SubExam subExam = subExamRepository.findById(subExamId)
                    .orElseThrow(() -> new ResourceNotFoundException("Subexamen no encontrado con ID: " + subExamId));

            subExam.setOrdenVisualizacion(i + 1);
            subExamRepository.save(subExam);
        }

        log.info("Sub-exams reordered successfully for exam: {}", examenId);
    }

    private SubExamResponse mapToResponse(SubExam subExam) {
        return SubExamResponse.builder()
                .id(subExam.getId())
                .examenId(subExam.getExamen().getId())
                .examenNombre(subExam.getExamen().getNombre())
                .examenCodigo(subExam.getExamen().getCodigo())
                .codigo(subExam.getCodigo())
                .nombre(subExam.getNombre())
                .tipoResultado(subExam.getTipoResultado())
                .unidadMedida(subExam.getUnidadMedida())
                .valorMinimo(subExam.getValorMinimo())
                .valorMaximo(subExam.getValorMaximo())
                .valorCriticoMin(subExam.getValorCriticoMin())
                .valorCriticoMax(subExam.getValorCriticoMax())
                .ordenVisualizacion(subExam.getOrdenVisualizacion())
                .observaciones(subExam.getObservaciones())
                .active(subExam.getActive())
                .createdAt(subExam.getCreatedAt())
                .updatedAt(subExam.getUpdatedAt())
                .build();
    }
}