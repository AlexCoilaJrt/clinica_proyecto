package com.pe.laboratorio.subexam.controller;

import java.util.List;

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

import com.pe.laboratorio.shared.dto.ApiResponse;
import com.pe.laboratorio.subexam.dto.SubExamRequest;
import com.pe.laboratorio.subexam.dto.SubExamResponse;
import com.pe.laboratorio.subexam.service.SubExamService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sub-exams")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SubExamController {
        private final SubExamService subExamService;

        @PostMapping
        @PreAuthorize("hasAuthority('EXAM_CREATE')")
        public ResponseEntity<ApiResponse<SubExamResponse>> create(
                        @Valid @RequestBody SubExamRequest request) {

                SubExamResponse response = subExamService.create(request);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.<SubExamResponse>builder()
                                                .success(true)
                                                .message("Subexamen creado exitosamente")
                                                .data(response)
                                                .build());
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasAuthority('EXAM_UPDATE')")
        public ResponseEntity<ApiResponse<SubExamResponse>> update(
                        @PathVariable Long id,
                        @Valid @RequestBody SubExamRequest request) {

                SubExamResponse response = subExamService.update(id, request);

                return ResponseEntity.ok(ApiResponse.<SubExamResponse>builder()
                                .success(true)
                                .message("Subexamen actualizado exitosamente")
                                .data(response)
                                .build());
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('EXAM_READ')")
        public ResponseEntity<ApiResponse<SubExamResponse>> getById(@PathVariable Long id) {
                SubExamResponse response = subExamService.getById(id);

                return ResponseEntity.ok(ApiResponse.<SubExamResponse>builder()
                                .success(true)
                                .data(response)
                                .build());
        }

        @GetMapping("/exam/{examenId}")
        @PreAuthorize("hasAuthority('EXAM_READ')")
        public ResponseEntity<ApiResponse<List<SubExamResponse>>> getByExamenId(
                        @PathVariable Long examenId) {

                List<SubExamResponse> subExams = subExamService.getByExamenId(examenId);

                return ResponseEntity.ok(ApiResponse.<List<SubExamResponse>>builder()
                                .success(true)
                                .message("Subexámenes obtenidos exitosamente")
                                .data(subExams)
                                .build());
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('EXAM_DELETE')")
        public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
                subExamService.delete(id);

                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .success(true)
                                .message("Subexamen eliminado exitosamente")
                                .build());
        }

        @PatchMapping("/{id}/status")
        @PreAuthorize("hasAuthority('EXAM_UPDATE')")
        public ResponseEntity<ApiResponse<Void>> toggleStatus(
                        @PathVariable Long id,
                        @RequestParam boolean active) {

                subExamService.toggleStatus(id, active);

                String message = active
                                ? "Subexamen activado exitosamente"
                                : "Subexamen desactivado exitosamente";

                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .success(true)
                                .message(message)
                                .build());
        }

        @PutMapping("/exam/{examenId}/reorder")
        @PreAuthorize("hasAuthority('EXAM_UPDATE')")
        public ResponseEntity<ApiResponse<Void>> reorder(
                        @PathVariable Long examenId,
                        @RequestBody List<Long> subExamIds) {

                subExamService.reorderSubExams(examenId, subExamIds);

                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .success(true)
                                .message("Subexámenes reordenados exitosamente")
                                .build());
        }
}