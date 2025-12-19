package com.pe.laboratorio.exam.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.laboratorio.exam.dto.ExamRequest;
import com.pe.laboratorio.exam.dto.ExamResponse;
import com.pe.laboratorio.exam.entity.Exam;
import com.pe.laboratorio.exam.repository.ExamRepository;
import com.pe.laboratorio.exam.service.ExamService;
import com.pe.laboratorio.examtype.entity.ExamType;
import com.pe.laboratorio.examtype.repository.ExamTypeRepository;
import com.pe.laboratorio.exception.ResourceNotFoundException;
import com.pe.laboratorio.exception.ValidationException;
import com.pe.laboratorio.labarea.entity.LabArea;
import com.pe.laboratorio.labarea.repository.LabAreaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final LabAreaRepository labAreaRepository;
    private final ExamTypeRepository examTypeRepository;

    @Override
    public ExamResponse create(ExamRequest request) {
        log.info("Creating exam with code: {}", request.getCodigo());

        if (examRepository.existsByCodigo(request.getCodigo())) {
            throw new ValidationException("Ya existe un examen con el código: " + request.getCodigo());
        }

        LabArea area = labAreaRepository.findById(request.getAreaId())
                .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada con ID: " + request.getAreaId()));

        ExamType tipoExamen = examTypeRepository.findById(request.getTipoExamenId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de examen no encontrado con ID: " + request.getTipoExamenId()));

        Exam exam = Exam.builder()
                .codigo(request.getCodigo().toUpperCase())
                .nombre(request.getNombre().toUpperCase())
                .area(area)
                .tipoExamen(tipoExamen)
                .metodo(request.getMetodo())
                .unidadMedida(request.getUnidadMedida())
                .tipoMuestra(request.getTipoMuestra())
                .tipoResultado(request.getTipoResultado())
                .precio(request.getPrecio())
                .valorMinimo(request.getValorMinimo())
                .valorMaximo(request.getValorMaximo())
                .valorCriticoMin(request.getValorCriticoMin())
                .valorCriticoMax(request.getValorCriticoMax())
                .tiempoEntrega(request.getTiempoEntrega())
                .indicaciones(request.getIndicaciones())
                .esPerfil(request.getEsPerfil())
                .active(true)
                .build();

        Exam saved = examRepository.save(exam);
        log.info("Exam created successfully with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    @Override
    public ExamResponse update(Long id, ExamRequest request) {
        log.info("Updating exam with ID: {}", id);

        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado con ID: " + id));

        if (examRepository.existsByCodigoAndIdNot(request.getCodigo(), id)) {
            throw new ValidationException("El código ya está en uso por otro examen");
        }

        LabArea area = labAreaRepository.findById(request.getAreaId())
                .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada con ID: " + request.getAreaId()));

        ExamType tipoExamen = examTypeRepository.findById(request.getTipoExamenId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de examen no encontrado con ID: " + request.getTipoExamenId()));

        exam.setCodigo(request.getCodigo().toUpperCase());
        exam.setNombre(request.getNombre().toUpperCase());
        exam.setArea(area);
        exam.setTipoExamen(tipoExamen);
        exam.setMetodo(request.getMetodo());
        exam.setUnidadMedida(request.getUnidadMedida());
        exam.setTipoMuestra(request.getTipoMuestra());
        exam.setTipoResultado(request.getTipoResultado());
        exam.setPrecio(request.getPrecio());
        exam.setValorMinimo(request.getValorMinimo());
        exam.setValorMaximo(request.getValorMaximo());
        exam.setValorCriticoMin(request.getValorCriticoMin());
        exam.setValorCriticoMax(request.getValorCriticoMax());
        exam.setTiempoEntrega(request.getTiempoEntrega());
        exam.setIndicaciones(request.getIndicaciones());
        exam.setEsPerfil(request.getEsPerfil());

        Exam updated = examRepository.save(exam);
        log.info("Exam updated successfully: {}", updated.getId());

        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResponse getById(Long id) {
        log.info("Getting exam by ID: {}", id);

        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado con ID: " + id));

        return mapToResponse(exam);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExamResponse> getAll(Pageable pageable) {
        log.info("Getting all exams - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        return examRepository.findByActiveTrue(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExamResponse> getByAreaId(Long areaId, Pageable pageable) {
        log.info("Getting exams by area ID: {}", areaId);

        if (!labAreaRepository.existsById(areaId)) {
            throw new ResourceNotFoundException("Área no encontrada con ID: " + areaId);
        }

        return examRepository.findByAreaIdAndActiveTrue(areaId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExamResponse> searchByNombreOrCodigo(String search, Pageable pageable) {
        log.info("Searching exams by: {}", search);

        return examRepository.searchByNombreOrCodigo(search, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExamResponse> getByTipoExamen(Long tipoExamenId, Pageable pageable) {
        log.info("Getting exams by tipo examen ID: {}", tipoExamenId);

        return examRepository.findByTipoExamenId(tipoExamenId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse> getPerfiles() {
        log.info("Getting all exam profiles");

        return examRepository.findByEsPerfilTrueAndActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse> getAllActive() {
        log.info("Getting all active exams");

        return examRepository.findByActiveTrue(Pageable.unpaged())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting exam with ID: {}", id);

        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado con ID: " + id));

        exam.setActive(false);
        examRepository.save(exam);

        log.info("Exam deleted (soft delete): {}", id);
    }

    @Override
    public void toggleStatus(Long id, boolean active) {
        log.info("Toggling status for exam ID: {} to {}", id, active);

        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado con ID: " + id));

        exam.setActive(active);
        examRepository.save(exam);

        log.info("Exam status changed: {} - active: {}", id, active);
    }

    private ExamResponse mapToResponse(Exam exam) {
        return ExamResponse.builder()
                .id(exam.getId())
                .codigo(exam.getCodigo())
                .nombre(exam.getNombre())
                .areaId(exam.getArea().getId())
                .areaNombre(exam.getArea().getDescripcion())
                .areaCodigo(exam.getArea().getCodigo())
                .tipoExamenId(exam.getTipoExamen().getId())
                .tipoExamenNombre(exam.getTipoExamen().getNombre())
                .metodo(exam.getMetodo())
                .unidadMedida(exam.getUnidadMedida())
                .tipoMuestra(exam.getTipoMuestra())
                .tipoResultado(exam.getTipoResultado())
                .precio(exam.getPrecio())
                .valorMinimo(exam.getValorMinimo())
                .valorMaximo(exam.getValorMaximo())
                .valorCriticoMin(exam.getValorCriticoMin())
                .valorCriticoMax(exam.getValorCriticoMax())
                .tiempoEntrega(exam.getTiempoEntrega())
                .indicaciones(exam.getIndicaciones())
                .active(exam.getActive())
                .esPerfil(exam.getEsPerfil())
                .createdAt(exam.getCreatedAt())
                .updatedAt(exam.getUpdatedAt())
                .build();
    }
}