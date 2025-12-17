package com.pe.laboratorio.labarea.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pe.laboratorio.labarea.dto.LabAreaRequest;
import com.pe.laboratorio.labarea.dto.LabAreaResponse;

public interface LabAreaService {

    LabAreaResponse create(LabAreaRequest request);

    LabAreaResponse update(Long id, LabAreaRequest request);

    LabAreaResponse getById(Long id);

    Page<LabAreaResponse> getAll(Pageable pageable);

    Page<LabAreaResponse> searchByDescripcion(String descripcion, Pageable pageable);

    List<LabAreaResponse> getAllActive();

    void delete(Long id);

    void toggleStatus(Long id, boolean active);
}