package com.pe.laboratorio.orders.repository;

import com.pe.laboratorio.orders.entity.LABOrdenDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LABOrdenDetalleRepository extends JpaRepository<LABOrdenDetalle, Long> {

    /**
     * Buscar todos los detalles de una orden específica
     */
    List<LABOrdenDetalle> findByOrdenId(Long ordenId);

    /**
     * Buscar detalles pendientes de validación primaria
     */
    @Query("SELECT d FROM LABOrdenDetalle d WHERE d.orden.id = :ordenId AND d.validadoPrimario = false")
    List<LABOrdenDetalle> findPendientesValidacionPrimaria(@Param("ordenId") Long ordenId);

    /**
     * Buscar detalles pendientes de validación final
     */
    @Query("SELECT d FROM LABOrdenDetalle d WHERE d.orden.id = :ordenId AND d.validadoPrimario = true AND d.validadoFinal = false")
    List<LABOrdenDetalle> findPendientesValidacionFinal(@Param("ordenId") Long ordenId);
}
