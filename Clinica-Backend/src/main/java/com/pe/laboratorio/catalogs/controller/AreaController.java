package com.pe.laboratorio.catalogs.controller;

import com.pe.laboratorio.catalogs.dto.AreaDTO;
import com.pe.laboratorio.catalogs.service.AreaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/catalogs/areas")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AreaDTO> createArea(@RequestBody AreaDTO request) {
        AreaDTO response = areaService.createArea(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AreaDTO>> getAllAreas() {
        List<AreaDTO> areas = areaService.getAllAreas();
        return ResponseEntity.ok(areas);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AreaDTO> updateArea(@PathVariable Long id, @RequestBody AreaDTO request) {
        AreaDTO response = areaService.updateArea(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteArea(@PathVariable Long id) {
        areaService.deleteArea(id);
        return ResponseEntity.noContent().build();
    }
}