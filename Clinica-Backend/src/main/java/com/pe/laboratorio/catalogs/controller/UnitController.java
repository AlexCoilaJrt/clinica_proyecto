package com.pe.laboratorio.catalogs.controller;

import com.pe.laboratorio.catalogs.dto.UnitDTO;
import com.pe.laboratorio.catalogs.service.UnitService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/catalogs/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnitDTO> createUnit(@RequestBody UnitDTO request) {
        UnitDTO response = unitService.createUnit(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UnitDTO>> getAllUnits() {
        List<UnitDTO> units = unitService.getAllUnits();
        return ResponseEntity.ok(units);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnitDTO> updateUnit(@PathVariable Long id, @RequestBody UnitDTO request) {
        UnitDTO response = unitService.updateUnit(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUnit(@PathVariable Long id) {
        unitService.deleteUnit(id);
        return ResponseEntity.noContent().build();
    }
}