package com.pe.laboratorio.orders.repository;

import com.pe.laboratorio.orders.entity.LABOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LABOrdenRepository extends JpaRepository<LABOrden, Long> {

        /**
         * Consulta nativa para filtrar órdenes con múltiples criterios opcionales.
         * Usa COALESCE para manejar valores nulos sin problemas de inferencia de tipos.
         */
        @Query(value = "SELECT DISTINCT o.* FROM lab_orden o " +
                        "LEFT JOIN patients p ON o.patient_id = p.id " +
                        "WHERE (CAST(:fechaInicio AS timestamp) IS NULL OR o.fecha_orden >= CAST(:fechaInicio AS timestamp)) "
                        +
                        "AND (CAST(:fechaFin AS timestamp) IS NULL OR o.fecha_orden <= CAST(:fechaFin AS timestamp)) " +
                        "AND (:patientId IS NULL OR o.patient_id = :patientId) " +
                        "AND (:medicoId IS NULL OR o.medico_id = :medicoId) " +
                        "AND (:estado IS NULL OR o.estado = :estado) " +
                        "AND (:prioridad IS NULL OR o.prioridad = :prioridad) " +
                        "ORDER BY o.fecha_orden DESC", nativeQuery = true)
        List<LABOrden> filtrarOrdenes(
                        @Param("fechaInicio") LocalDateTime fechaInicio,
                        @Param("fechaFin") LocalDateTime fechaFin,
                        @Param("patientId") Long patientId,
                        @Param("medicoId") Long medicoId,
                        @Param("estado") String estado,
                        @Param("prioridad") String prioridad);

        /**
         * Buscar todas las órdenes de un médico específico.
         * Usado para aplicar RF-MED-01: Médicos solo ven sus propias órdenes.
         */
        @Query("SELECT o FROM LABOrden o WHERE o.medico.id = :medicoId ORDER BY o.fechaOrden DESC")
        List<LABOrden> findByMedicoId(@Param("medicoId") Long medicoId);

        /**
         * Buscar órdenes por paciente
         */
        @Query("SELECT o FROM LABOrden o WHERE o.patient.id = :patientId ORDER BY o.fechaOrden DESC")
        List<LABOrden> findByPatientId(@Param("patientId") Long patientId);

        /**
         * Buscar órdenes por estado
         */
        List<LABOrden> findByEstadoOrderByFechaOrdenDesc(LABOrden.EstadoOrden estado);

        /**
         * Buscar órdenes por prioridad
         */
        List<LABOrden> findByPrioridadOrderByFechaOrdenDesc(LABOrden.PrioridadOrden prioridad);

        /**
         * Buscar orden por número de orden
         */
        Optional<LABOrden> findByNumeroOrden(String numeroOrden);

        /**
         * Obtener el último número de orden para generar el siguiente
         */
        @Query("SELECT o FROM LABOrden o ORDER BY o.id DESC LIMIT 1")
        Optional<LABOrden> findLastOrden();
}
