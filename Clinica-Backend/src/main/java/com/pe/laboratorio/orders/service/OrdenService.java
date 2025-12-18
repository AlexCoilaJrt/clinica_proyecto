package com.pe.laboratorio.orders.service;

import com.pe.laboratorio.orders.dto.OrdenCreateDTO;
import com.pe.laboratorio.orders.dto.OrdenFilterDTO;
import com.pe.laboratorio.orders.dto.OrdenResponseDTO;

import java.util.List;

/**
 * Interface del servicio de órdenes de laboratorio
 */
public interface OrdenService {

    /**
     * Filtrar órdenes según criterios y rol del usuario
     * Aplica reglas de negocio:
     * - Médicos solo ven sus propias órdenes (RF-MED-01)
     * - Tecnólogos y Biólogos ven todas las órdenes (RF-TEC-05, RF-BIO-03)
     */
    List<OrdenResponseDTO> filtrarOrdenes(OrdenFilterDTO filtro);

    /**
     * Crear una nueva orden de laboratorio
     */
    OrdenResponseDTO crearOrden(OrdenCreateDTO dto);

    /**
     * Obtener una orden por ID
     */
    OrdenResponseDTO obtenerOrdenPorId(Long id);

    /**
     * Actualizar el estado de una orden
     */
    OrdenResponseDTO actualizarEstado(Long id, String nuevoEstado);

    /**
     * Obtener todas las órdenes (sin filtros)
     */
    List<OrdenResponseDTO> obtenerTodasLasOrdenes();
}
