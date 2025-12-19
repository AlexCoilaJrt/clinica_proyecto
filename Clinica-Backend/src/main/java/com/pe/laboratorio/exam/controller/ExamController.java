package com.pe.laboratorio.exam.controller;

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

import com.pe.laboratorio.exam.dto.ExamRequest;
import com.pe.laboratorio.exam.dto.ExamResponse;
import com.pe.laboratorio.exam.service.ExamService;
import com.pe.laboratorio.shared.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/exams")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ExamController {

        private final ExamService examService;

        @PostMapping
        @PreAuthorize("hasAuthority('EXAM_CREATE')")
        public ResponseEntity<ApiResponse<ExamResponse>> create(
                        @Valid @RequestBody ExamRequest request) {

                ExamResponse response = examService.create(request);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.<ExamResponse>builder()
                                                .success(true)
                                                .message("Examen creado exitosamente")
                                                .data(response)
                                                .build());
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasAuthority('EXAM_UPDATE')")
        public ResponseEntity<ApiResponse<ExamResponse>> update(
                        @PathVariable Long id,
                        @Valid @RequestBody ExamRequest request) {

                ExamResponse response = examService.update(id, request);

                return ResponseEntity.ok(ApiResponse.<ExamResponse>builder()
                                .success(true)
                                .message("Examen actualizado exitosamente")
                                .data(response)
                                .build());
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('EXAM_READ')")
        public ResponseEntity<ApiResponse<ExamResponse>> getById(@PathVariable Long id) {
                ExamResponse response = examService.getById(id);

                return ResponseEntity.ok(ApiResponse.<ExamResponse>builder()
                                .success(true)
                                .data(response)
                                .build());
        }

        @GetMapping
        @PreAuthorize("hasAuthority('EXAM_READ')")
        public ResponseEntity<ApiResponse<Page<ExamResponse>>> getAll(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "nombre") String sort,
                        @RequestParam(defaultValue = "asc") String direction) {

                Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
                                ? Sort.Direction.DESC
                                : Sort.Direction.ASC;

                Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
                Page<ExamResponse> exams = examService.getAll(pageable);

                return ResponseEntity.ok(ApiResponse.<Page<ExamResponse>>builder()
                                .success(true)
                                .message("Exámenes obtenidos exitosamente")
                                .data(exams)
                                .build());
        }

        @GetMapping("/area/{areaId}")
        @PreAuthorize("hasAuthority('EXAM_READ')")
        public ResponseEntity<ApiResponse<Page<ExamResponse>>> getByAreaId(
                        @PathVariable Long areaId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "nombre"));
                Page<ExamResponse> exams = examService.getByAreaId(areaId, pageable);

                return ResponseEntity.ok(ApiResponse.<Page<ExamResponse>>builder()
                                .success(true)
                                .message("Exámenes del área obtenidos exitosamente")
                                .data(exams)
                                .build());
        }

        @GetMapping("/search")
        @PreAuthorize("hasAuthority('EXAM_READ')")
        public ResponseEntity<ApiResponse<Page<ExamResponse>>> search(
                        @RequestParam String q,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                Pageable pageable = PageRequest.of(page, size);
                Page<ExamResponse> exams = examService.searchByNombreOrCodigo(q, pageable);

                return ResponseEntity.ok(ApiResponse.<Page<ExamResponse>>builder()
                                .success(true)
                                .message("Búsqueda realizada exitosamente")
                                .data(exams)
                                .build());
        }

        @GetMapping("/tipo/{tipoExamenId}")
        @PreAuthorize("hasAuthority('EXAM_READ')")
        public ResponseEntity<ApiResponse<Page<ExamResponse>>> getByTipoExamen(
                        @PathVariable Long tipoExamenId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                Pageable pageable = PageRequest.of(page, size);
                Page<ExamResponse> exams = examService.getByTipoExamen(tipoExamenId, pageable);

                return ResponseEntity.ok(ApiResponse.<Page<ExamResponse>>builder()
                                .success(true)
                                .message("Exámenes por tipo obtenidos exitosamente")
                                .data(exams)
                                .build());
        }

        @GetMapping("/perfiles")
        @PreAuthorize("hasAuthority('EXAM_READ')")
        public ResponseEntity<ApiResponse<List<ExamResponse>>> getPerfiles() {
                List<ExamResponse> perfiles = examService.getPerfiles();

                return ResponseEntity.ok(ApiResponse.<List<ExamResponse>>builder()
                                .success(true)
                                .message("Perfiles obtenidos exitosamente")
                                .data(perfiles)
                                .build());
        }

        @GetMapping("/active")
        @PreAuthorize("hasAuthority('EXAM_READ')")
        public ResponseEntity<ApiResponse<List<ExamResponse>>> getAllActive() {
                List<ExamResponse> exams = examService.getAllActive();

                return ResponseEntity.ok(ApiResponse.<List<ExamResponse>>builder()
                                .success(true)
                                .message("Exámenes activos obtenidos exitosamente")
                                .data(exams)
                                .build());
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('EXAM_DELETE')")
        public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
                examService.delete(id);

                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .success(true)
                                .message("Examen eliminado exitosamente")
                                .build());
        }

        @PatchMapping("/{id}/status")
        @PreAuthorize("hasAuthority('EXAM_UPDATE')")
        public ResponseEntity<ApiResponse<Void>> toggleStatus(
                        @PathVariable Long id,
                        @RequestParam boolean active) {

                examService.toggleStatus(id, active);

                String message = active
                                ? "Examen activado exitosamente"
                                : "Examen desactivado exitosamente";

                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .success(true)
                                .message(message)
                                .build());
        }
}