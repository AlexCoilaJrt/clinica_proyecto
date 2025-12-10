package com.pe.laboratorio.catalogs.service;

import com.pe.laboratorio.catalogs.dto.AreaDTO;
import com.pe.laboratorio.catalogs.entity.Area;
import com.pe.laboratorio.catalogs.repository.AreaRepository;
import com.pe.laboratorio.exception.ResourceNotFoundException;
import com.pe.laboratorio.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;

    public AreaDTO mapToDTO(Area area) {
        return AreaDTO.builder()
                .id(area.getId())
                .name(area.getName())
                .description(area.getDescription())
                .isEnabled(area.getIsEnabled())
                .build();
    }

    public Area mapToEntity(AreaDTO dto) {
        return Area.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .isEnabled(Optional.ofNullable(dto.getIsEnabled()).orElse(true))
                .build();
    }

    public AreaDTO createArea(AreaDTO dto) {
        if (areaRepository.findByName(dto.getName()).isPresent()) {
            throw new ValidationException("El área con el nombre " + dto.getName() + " ya existe.");
        }
        Area newArea = mapToEntity(dto);
        Area savedArea = areaRepository.save(newArea);
        return mapToDTO(savedArea);
    }

    public List<AreaDTO> getAllAreas() {
        return areaRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AreaDTO updateArea(Long id, AreaDTO dto) {
        Area existingArea = areaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada con ID: " + id));

        if (!existingArea.getName().equals(dto.getName()) && areaRepository.findByName(dto.getName()).isPresent()) {
            throw new ValidationException("El área con el nombre " + dto.getName() + " ya existe.");
        }

        existingArea.setName(dto.getName());
        existingArea.setDescription(dto.getDescription());
        existingArea.setIsEnabled(Optional.ofNullable(dto.getIsEnabled()).orElse(existingArea.getIsEnabled()));

        Area updatedArea = areaRepository.save(existingArea);
        return mapToDTO(updatedArea);
    }

    public void deleteArea(Long id) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada con ID: " + id));

        area.setIsEnabled(false);
        areaRepository.save(area);
    }
}