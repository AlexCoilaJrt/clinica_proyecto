package com.pe.laboratorio.catalogs.service;

import com.pe.laboratorio.catalogs.dto.ExamTypeDTO;
import com.pe.laboratorio.catalogs.entity.ExamType;
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
public class ExamTypeService {

    private final ExamTypeRepository examTypeRepository;

    public ExamTypeDTO mapToDTO(ExamType examType) {
        return ExamTypeDTO.builder()
                .id(examType.getId())
                .name(examType.getName())
                .description(examType.getDescription())
                .isEnabled(examType.getIsEnabled())
                .build();
    }

    public ExamType mapToEntity(ExamTypeDTO dto) {
        return ExamType.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .isEnabled(Optional.ofNullable(dto.getIsEnabled()).orElse(true))
                .build();
    }

    public ExamTypeDTO createExamType(ExamTypeDTO dto) {
        if (examTypeRepository.findByName(dto.getName()).isPresent()) {
            throw new ValidationException("El tipo de examen con el nombre " + dto.getName() + " ya existe.");
        }
        ExamType newExamType = mapToEntity(dto);
        ExamType savedExamType = examTypeRepository.save(newExamType);
        return mapToDTO(savedExamType);
    }

    public List<ExamTypeDTO> getAllExamTypes() {
        return examTypeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ExamTypeDTO updateExamType(Long id, ExamTypeDTO dto) {
        ExamType existingExamType = examTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de examen no encontrado con ID: " + id));

        if (!existingExamType.getName().equals(dto.getName()) && examTypeRepository.findByName(dto.getName()).isPresent()) {
            throw new ValidationException("El tipo de examen con el nombre " + dto.getName() + " ya existe.");
        }

        existingExamType.setName(dto.getName());
        existingExamType.setDescription(dto.getDescription());
        existingExamType.setIsEnabled(Optional.ofNullable(dto.getIsEnabled()).orElse(existingExamType.getIsEnabled()));

        ExamType updatedExamType = examTypeRepository.save(existingExamType);
        return mapToDTO(updatedExamType);
    }

    public void deleteExamType(Long id) {
        ExamType examType = examTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de examen no encontrado con ID: " + id));

        examType.setIsEnabled(false);
        examTypeRepository.save(examType);
    }
}