package com.pe.laboratorio.examtype.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pe.laboratorio.examtype.dto.ExamTypeRequest;
import com.pe.laboratorio.examtype.dto.ExamTypeResponse;
import com.pe.laboratorio.examtype.service.ExamTypeService;
import com.pe.laboratorio.shared.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/exam-types")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ExamTypeController {

        private final ExamTypeService examTypeService;

        @PostMapping
        @PreAuthorize("hasAuthority('EXAM_TYPE_CREATE')")
        public ResponseEntity<ApiResponse<ExamTypeResponse>> create(
                        @Valid @RequestBody ExamTypeRequest request) {

                ExamTypeResponse response = examTypeService.create(request);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.<ExamTypeResponse>builder()
                                                .success(true)
                                                .message("Tipo de examen creado exitosamente")
                                                .data(response)
                                                .build());
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasAuthority('EXAM_TYPE_UPDATE')")
        public ResponseEntity<ApiResponse<ExamTypeResponse>> update(
                        @PathVariable Long id,
                        @Valid @RequestBody ExamTypeRequest request) {

                ExamTypeResponse response = examTypeService.update(id, request);

                return ResponseEntity.ok(ApiResponse.<ExamTypeResponse>builder()
                                .success(true)
                                .message("Tipo de examen actualizado exitosamente")
                                .data(response)
                                .build());
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('EXAM_TYPE_READ')")
        public ResponseEntity<ApiResponse<ExamTypeResponse>> getById(@PathVariable Long id) {
                ExamTypeResponse response = examTypeService.getById(id);

                return ResponseEntity.ok(ApiResponse.<ExamTypeResponse>builder()
                                .success(true)
                                .data(response)
                                .build());
        }

        @GetMapping("/nombre/{nombre}")
        @PreAuthorize("hasAuthority('EXAM_TYPE_READ')")
        public ResponseEntity<ApiResponse<ExamTypeResponse>> getByNombre(@PathVariable String nombre) {
                ExamTypeResponse response = examTypeService.getByNombre(nombre);

                return ResponseEntity.ok(ApiResponse.<ExamTypeResponse>builder()
                                .success(true)
                                .data(response)
                                .build());
        }

        @GetMapping
        @PreAuthorize("hasAuthority('EXAM_TYPE_READ')")
        public ResponseEntity<ApiResponse<Page<ExamTypeResponse>>> getAll(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "nombre") String sort,
                        @RequestParam(defaultValue = "asc") String direction) {

                Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
                                ? Sort.Direction.DESC
                                : Sort.Direction.ASC;

                Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
                Page<ExamTypeResponse> examTypes = examTypeService.getAll(pageable);

                return ResponseEntity.ok(ApiResponse.<Page<ExamTypeResponse>>builder()
                                .success(true)
                                .message("Tipos de examen obtenidos exitosamente")
                                .data(examTypes)
                                .build());
        }

        @GetMapping("/active")
        @PreAuthorize("hasAuthority('EXAM_TYPE_READ')")
        public ResponseEntity<ApiResponse<List<ExamTypeResponse>>> getAllActive() {
                List<ExamTypeResponse> examTypes = examTypeService.getAllActive();

                return ResponseEntity.ok(ApiResponse.<List<ExamTypeResponse>>builder()
                                .success(true)
                                .message("Tipos de examen activos obtenidos exitosamente")
                                .data(examTypes)
                                .build());
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('EXAM_TYPE_DELETE')")
        public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
                examTypeService.delete(id);

                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .success(true)
                                .message("Tipo de examen eliminado exitosamente")
                                .build());
        }

        @PatchMapping("/{id}/status")
        @PreAuthorize("hasAuthority('EXAM_TYPE_UPDATE')")
        public ResponseEntity<ApiResponse<Void>> toggleStatus(
                        @PathVariable Long id,
                        @RequestParam boolean active) {

                examTypeService.toggleStatus(id, active);

                String message = active
                                ? "Tipo de examen activado exitosamente"
                                : "Tipo de examen desactivado exitosamente";

                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .success(true)
                                .message(message)
                                .build());
        }
}