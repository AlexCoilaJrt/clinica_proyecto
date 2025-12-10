package com.pe.laboratorio.catalogs.service;

import com.pe.laboratorio.catalogs.dto.ExamDTO;
import com.pe.laboratorio.catalogs.entity.Area;
import com.pe.laboratorio.catalogs.entity.Exam;
import com.pe.laboratorio.catalogs.entity.ExamType;
import com.pe.laboratorio.catalogs.repository.AreaRepository;
import com.pe.laboratorio.catalogs.repository.ExamRepository;
import com.pe.laboratorio.catalogs.repository.ExamTypeRepository;
import com.pe.laboratorio.exception.ResourceNotFoundException;
import com.pe.laboratorio.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final AreaRepository areaRepository;
    private final ExamTypeRepository examTypeRepository;
    private final AreaService areaService;
    private final ExamTypeService examTypeService;

    private ExamDTO mapToDTO(Exam exam) {
        return ExamDTO.builder()
                .id(exam.getId())
                .name(exam.getName())
                .description(exam.getDescription())
                .areaId(exam.getArea().getId())
                .examTypeId(exam.getExamType().getId())
                .area(areaService.mapToDTO(exam.getArea()))
                .examType(examTypeService.mapToDTO(exam.getExamType()))
                .isEnabled(exam.getIsEnabled())
                .build();
    }

    private Exam mapToEntity(ExamDTO dto) {
        Area area = areaRepository.findById(dto.getAreaId())
                .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada con ID: " + dto.getAreaId()));

        ExamType examType = examTypeRepository.findById(dto.getExamTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de Examen no encontrado con ID: " + dto.getExamTypeId()));

        return Exam.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .area(area)
                .examType(examType)
                .isEnabled(Optional.ofNullable(dto.getIsEnabled()).orElse(true))
                .build();
    }

    public ExamDTO createExam(ExamDTO dto) {
        if (examRepository.findByName(dto.getName()).isPresent()) {
            throw new ValidationException("El examen con el nombre " + dto.getName() + " ya existe.");
        }
        Exam newExam = mapToEntity(dto);
        Exam savedExam = examRepository.save(newExam);
        return mapToDTO(savedExam);
    }

    public List<ExamDTO> getAllExams() {
        return examRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ExamDTO updateExam(Long id, ExamDTO dto) {
        Exam existingExam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado con ID: " + id));

        if (!existingExam.getName().equals(dto.getName()) && examRepository.findByName(dto.getName()).isPresent()) {
            throw new ValidationException("El examen con el nombre " + dto.getName() + " ya existe.");
        }

        if (dto.getAreaId() != null && !dto.getAreaId().equals(existingExam.getArea().getId())) {
            Area newArea = areaRepository.findById(dto.getAreaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada con ID: " + dto.getAreaId()));
            existingExam.setArea(newArea);
        }
        if (dto.getExamTypeId() != null && !dto.getExamTypeId().equals(existingExam.getExamType().getId())) {
            ExamType newExamType = examTypeRepository.findById(dto.getExamTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo de Examen no encontrado con ID: " + dto.getExamTypeId()));
            existingExam.setExamType(newExamType);
        }

        existingExam.setName(dto.getName());
        existingExam.setDescription(dto.getDescription());
        existingExam.setIsEnabled(Optional.ofNullable(dto.getIsEnabled()).orElse(existingExam.getIsEnabled()));

        Exam updatedExam = examRepository.save(existingExam);
        return mapToDTO(updatedExam);
    }

    public void deleteExam(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado con ID: " + id));

        exam.setIsEnabled(false);
        examRepository.save(exam);
    }
}