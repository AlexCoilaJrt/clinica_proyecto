package com.pe.laboratorio.orders.controller;

import com.pe.laboratorio.orders.dto.OrdenCreateDTO;
import com.pe.laboratorio.orders.dto.OrdenFilterDTO;
import com.pe.laboratorio.orders.dto.OrdenResponseDTO;
import com.pe.laboratorio.orders.service.OrdenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenController {

    private final OrdenService ordenService;

    /**
     * Endpoint para filtrar órdenes según criterios
     * Accesible para: ADMIN, MEDICO, TECNOLOGO, BIOLOGO
     */
    @PostMapping("/filtrar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MEDICO', 'TECNOLOGO_MEDICO', 'BIOLOGO')")
    public ResponseEntity<List<OrdenResponseDTO>> filtrarOrdenes(@RequestBody OrdenFilterDTO filtro) {
        List<OrdenResponseDTO> ordenes = ordenService.filtrarOrdenes(filtro);
        return ResponseEntity.ok(ordenes);
    }

    /**
     * Crear una nueva orden de laboratorio
     * Accesible para: ADMIN, MEDICO
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MEDICO')")
    public ResponseEntity<OrdenResponseDTO> crearOrden(@RequestBody OrdenCreateDTO dto) {
        OrdenResponseDTO orden = ordenService.crearOrden(dto);
        return ResponseEntity.status(201).body(orden);
    }

    /**
     * Obtener una orden por ID
     * Accesible para: ADMIN, MEDICO, TECNOLOGO, BIOLOGO
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MEDICO', 'TECNOLOGO_MEDICO', 'BIOLOGO')")
    public ResponseEntity<OrdenResponseDTO> obtenerOrden(@PathVariable Long id) {
        OrdenResponseDTO orden = ordenService.obtenerOrdenPorId(id);
        return ResponseEntity.ok(orden);
    }

    /**
     * Actualizar el estado de una orden
     * Accesible para: ADMIN, TECNOLOGO, BIOLOGO
     */
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TECNOLOGO_MEDICO', 'BIOLOGO')")
    public ResponseEntity<OrdenResponseDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String nuevoEstado = body.get("estado");
        OrdenResponseDTO orden = ordenService.actualizarEstado(id, nuevoEstado);
        return ResponseEntity.ok(orden);
    }

    /**
     * Obtener todas las órdenes (sin filtros)
     * Accesible para: ADMIN, MEDICO, TECNOLOGO, BIOLOGO
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MEDICO', 'TECNOLOGO_MEDICO', 'BIOLOGO')")
    public ResponseEntity<List<OrdenResponseDTO>> obtenerTodasLasOrdenes() {
        List<OrdenResponseDTO> ordenes = ordenService.obtenerTodasLasOrdenes();
        return ResponseEntity.ok(ordenes);
    }
}
