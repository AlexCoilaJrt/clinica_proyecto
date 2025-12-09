package com.pe.laboratorio.catalogs.service;

import com.pe.laboratorio.catalogs.dto.UnitDTO;
import com.pe.laboratorio.catalogs.entity.Unit;
import com.pe.laboratorio.catalogs.repository.UnitRepository;
import com.pe.laboratorio.exception.ResourceNotFoundException;
import com.pe.laboratorio.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;

    private UnitDTO mapToDTO(Unit unit) {
        return UnitDTO.builder()
                .id(unit.getId())
                .name(unit.getName())
                .description(unit.getDescription())
                .isEnabled(unit.getIsEnabled())
                .build();
    }

    private Unit mapToEntity(UnitDTO dto) {
        return Unit.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .isEnabled(Optional.ofNullable(dto.getIsEnabled()).orElse(true))
                .build();
    }

    public UnitDTO createUnit(UnitDTO dto) {
        if (unitRepository.findByName(dto.getName()).isPresent()) {
            throw new ValidationException("La unidad con el nombre " + dto.getName() + " ya existe.");
        }
        Unit newUnit = mapToEntity(dto);
        Unit savedUnit = unitRepository.save(newUnit);
        return mapToDTO(savedUnit);
    }


    public List<UnitDTO> getAllUnits() {
        return unitRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public UnitDTO updateUnit(Long id, UnitDTO dto) {
        Unit existingUnit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad no encontrada con ID: " + id));

        if (!existingUnit.getName().equals(dto.getName()) && unitRepository.findByName(dto.getName()).isPresent()) {
            throw new ValidationException("La unidad con el nombre " + dto.getName() + " ya existe.");
        }

        existingUnit.setName(dto.getName());
        existingUnit.setDescription(dto.getDescription());
        existingUnit.setIsEnabled(Optional.ofNullable(dto.getIsEnabled()).orElse(existingUnit.getIsEnabled()));

        Unit updatedUnit = unitRepository.save(existingUnit);
        return mapToDTO(updatedUnit);
    }

    public void deleteUnit(Long id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad no encontrada con ID: " + id));

        unit.setIsEnabled(false);
        unitRepository.save(unit);
    }
}