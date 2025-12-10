package com.pe.laboratorio.catalogs.controller;

import com.pe.laboratorio.catalogs.dto.ExamTypeDTO;
import com.pe.laboratorio.catalogs.service.ExamTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/catalogs/exam-types")
@RequiredArgsConstructor
public class ExamTypeController {

    private final ExamTypeService examTypeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExamTypeDTO> createExamType(@RequestBody ExamTypeDTO request) {
        ExamTypeDTO response = examTypeService.createExamType(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ExamTypeDTO>> getAllExamTypes() {
        List<ExamTypeDTO> examTypes = examTypeService.getAllExamTypes();
        return ResponseEntity.ok(examTypes);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExamTypeDTO> updateExamType(@PathVariable Long id, @RequestBody ExamTypeDTO request) {
        ExamTypeDTO response = examTypeService.updateExamType(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteExamType(@PathVariable Long id) {
        examTypeService.deleteExamType(id);
        return ResponseEntity.noContent().build();
    }
}