package com.pe.laboratorio.labarea.controller;

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

import com.pe.laboratorio.labarea.dto.LabAreaRequest;
import com.pe.laboratorio.labarea.dto.LabAreaResponse;
import com.pe.laboratorio.labarea.service.LabAreaService;
import com.pe.laboratorio.shared.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lab-areas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LabAreaController {

        private final LabAreaService labAreaService;

        /**
         * Crear área de laboratorio
         * POST /api/v1/lab-areas
         */
        @PostMapping
        @PreAuthorize("hasAuthority('LAB_AREA_CREATE')")
        public ResponseEntity<ApiResponse<LabAreaResponse>> create(
                        @Valid @RequestBody LabAreaRequest request) {

                LabAreaResponse response = labAreaService.create(request);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.<LabAreaResponse>builder()
                                                .success(true)
                                                .message("Área de laboratorio creada exitosamente")
                                                .data(response)
                                                .build());
        }

        /**
         * Actualizar área de laboratorio
         * PUT /api/v1/lab-areas/{id}
         */
        @PutMapping("/{id}")
        @PreAuthorize("hasAuthority('LAB_AREA_UPDATE')")
        public ResponseEntity<ApiResponse<LabAreaResponse>> update(
                        @PathVariable Long id,
                        @Valid @RequestBody LabAreaRequest request) {

                LabAreaResponse response = labAreaService.update(id, request);

                return ResponseEntity.ok(ApiResponse.<LabAreaResponse>builder()
                                .success(true)
                                .message("Área de laboratorio actualizada exitosamente")
                                .data(response)
                                .build());
        }

        /**
         * Obtener área por ID
         * GET /api/v1/lab-areas/{id}
         */
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('LAB_AREA_READ')")
        public ResponseEntity<ApiResponse<LabAreaResponse>> getById(@PathVariable Long id) {
                LabAreaResponse response = labAreaService.getById(id);

                return ResponseEntity.ok(ApiResponse.<LabAreaResponse>builder()
                                .success(true)
                                .data(response)
                                .build());
        }

        /**
         * Listar todas las áreas (paginado)
         * GET /api/v1/lab-areas?page=0&size=10&sort=descripcion,asc
         */
        @GetMapping
        @PreAuthorize("hasAuthority('LAB_AREA_READ')")
        public ResponseEntity<ApiResponse<Page<LabAreaResponse>>> getAll(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "id") String sort,
                        @RequestParam(defaultValue = "asc") String direction) {

                Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
                                ? Sort.Direction.DESC
                                : Sort.Direction.ASC;

                Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
                Page<LabAreaResponse> areas = labAreaService.getAll(pageable);

                return ResponseEntity.ok(ApiResponse.<Page<LabAreaResponse>>builder()
                                .success(true)
                                .message("Áreas obtenidas exitosamente")
                                .data(areas)
                                .build());
        }

        /**
         * Buscar áreas por descripción
         * GET /api/v1/lab-areas/search?q=bioquimica&page=0&size=10
         */
        @GetMapping("/search")
        @PreAuthorize("hasAuthority('LAB_AREA_READ')")
        public ResponseEntity<ApiResponse<Page<LabAreaResponse>>> search(
                        @RequestParam String q,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                Pageable pageable = PageRequest.of(page, size);
                Page<LabAreaResponse> areas = labAreaService.searchByDescripcion(q, pageable);

                return ResponseEntity.ok(ApiResponse.<Page<LabAreaResponse>>builder()
                                .success(true)
                                .message("Búsqueda realizada exitosamente")
                                .data(areas)
                                .build());
        }

        /**
         * Listar todas las áreas activas (sin paginación)
         * GET /api/v1/lab-areas/active
         */
        @GetMapping("/active")
        @PreAuthorize("hasAuthority('LAB_AREA_READ')")
        public ResponseEntity<ApiResponse<List<LabAreaResponse>>> getAllActive() {
                List<LabAreaResponse> areas = labAreaService.getAllActive();

                return ResponseEntity.ok(ApiResponse.<List<LabAreaResponse>>builder()
                                .success(true)
                                .message("Áreas activas obtenidas exitosamente")
                                .data(areas)
                                .build());
        }

        /**
         * Eliminar área (soft delete)
         * DELETE /api/v1/lab-areas/{id}
         */
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('LAB_AREA_DELETE')")
        public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
                labAreaService.delete(id);

                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .success(true)
                                .message("Área de laboratorio eliminada exitosamente")
                                .build());
        }

        /**
         * Activar/Desactivar área
         * PATCH /api/v1/lab-areas/{id}/status?active=true
         */
        @PatchMapping("/{id}/status")
        @PreAuthorize("hasAuthority('LAB_AREA_UPDATE')")
        public ResponseEntity<ApiResponse<Void>> toggleStatus(
                        @PathVariable Long id,
                        @RequestParam boolean active) {

                labAreaService.toggleStatus(id, active);

                String message = active
                                ? "Área activada exitosamente"
                                : "Área desactivada exitosamente";

                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .success(true)
                                .message(message)
                                .build());
        }
}