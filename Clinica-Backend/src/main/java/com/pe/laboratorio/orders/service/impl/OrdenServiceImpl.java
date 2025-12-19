package com.pe.laboratorio.orders.service.impl;

import com.pe.laboratorio.exam.entity.Exam;
import com.pe.laboratorio.exam.repository.ExamRepository;
import com.pe.laboratorio.exception.ResourceNotFoundException;
import com.pe.laboratorio.orders.dto.OrdenCreateDTO;
import com.pe.laboratorio.orders.dto.OrdenFilterDTO;
import com.pe.laboratorio.orders.dto.OrdenResponseDTO;
import com.pe.laboratorio.orders.entity.LABOrden;
import com.pe.laboratorio.orders.entity.LABOrdenDetalle;
import com.pe.laboratorio.orders.repository.LABOrdenRepository;
import com.pe.laboratorio.orders.service.OrdenService;
import com.pe.laboratorio.patients.entity.Patient;
import com.pe.laboratorio.patients.repository.PatientRepository;
import com.pe.laboratorio.users.entity.DatosPersonales;
import com.pe.laboratorio.users.repository.DatosPersonalesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdenServiceImpl implements OrdenService {

    private final LABOrdenRepository ordenRepository;
    private final PatientRepository patientRepository;
    private final DatosPersonalesRepository datosPersonalesRepository;
    private final ExamRepository examRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrdenResponseDTO> filtrarOrdenes(OrdenFilterDTO filtro) {
        // Convertir LocalDate a LocalDateTime
        LocalDateTime fechaInicio = filtro.getFechaInicio() != null
                ? LocalDateTime.of(filtro.getFechaInicio(), LocalTime.MIN)
                : null;
        LocalDateTime fechaFin = filtro.getFechaFin() != null ? LocalDateTime.of(filtro.getFechaFin(), LocalTime.MAX)
                : null;

        // Obtener usuario autenticado
        DatosPersonales usuarioActual = obtenerUsuarioActual();

        List<LABOrden> ordenes;

        // Aplicar reglas de negocio según rol
        if (esMedico(usuarioActual)) {
            // RF-MED-01: Médicos solo ven órdenes que ellos registraron
            log.info("Usuario {} es médico, aplicando filtro por medicoId", usuarioActual.getLogin());
            ordenes = ordenRepository.filtrarOrdenes(
                    fechaInicio,
                    fechaFin,
                    filtro.getPatientId(),
                    usuarioActual.getId(), // Forzar filtro por médico actual
                    filtro.getEstado(),
                    filtro.getPrioridad());
        } else if (esTecnologoOBiologo(usuarioActual)) {
            // RF-TEC-05, RF-BIO-03: Tecnólogos y Biólogos ven todas las órdenes
            log.info("Usuario {} es tecnólogo/biólogo, sin restricciones", usuarioActual.getLogin());
            ordenes = ordenRepository.filtrarOrdenes(
                    fechaInicio,
                    fechaFin,
                    filtro.getPatientId(),
                    filtro.getMedicoId(),
                    filtro.getEstado(),
                    filtro.getPrioridad());
        } else {
            // Otros roles: sin restricciones (ADMIN, etc.)
            log.info("Usuario {} tiene acceso completo", usuarioActual.getLogin());
            ordenes = ordenRepository.filtrarOrdenes(
                    fechaInicio,
                    fechaFin,
                    filtro.getPatientId(),
                    filtro.getMedicoId(),
                    filtro.getEstado(),
                    filtro.getPrioridad());
        }

        // Registrar auditoría (RN-SIS-02)
        log.info("Usuario {} consultó {} órdenes con filtros: {}",
                usuarioActual.getLogin(), ordenes.size(), filtro);

        return ordenes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrdenResponseDTO crearOrden(OrdenCreateDTO dto) {
        DatosPersonales usuarioActual = obtenerUsuarioActual();

        // Validar y obtener entidades relacionadas
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Paciente no encontrado con ID: " + dto.getPatientId()));

        DatosPersonales medico = null;
        if (dto.getMedicoId() != null) {
            medico = datosPersonalesRepository.findById(dto.getMedicoId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Médico no encontrado con ID: " + dto.getMedicoId()));
        }

        // Generar número de orden
        String numeroOrden = generarNumeroOrden();

        // Crear orden
        LABOrden orden = LABOrden.builder()
                .patient(patient)
                .medico(medico)
                .user(usuarioActual)
                .numeroOrden(numeroOrden)
                .diagnostico(dto.getDiagnostico())
                .prioridad(LABOrden.PrioridadOrden.valueOf(dto.getPrioridad()))
                .tipoAtencion(dto.getTipoAtencion())
                .tipoMuestra(dto.getTipoMuestra())
                .observaciones(dto.getObservaciones())
                .fechaEntrega(dto.getFechaEntrega())
                .createdBy(usuarioActual)
                .build();

        // Agregar detalles (exámenes) y calcular total
        BigDecimal total = BigDecimal.ZERO;
        if (dto.getExamenesIds() != null && !dto.getExamenesIds().isEmpty()) {
            for (Long examId : dto.getExamenesIds()) {
                Exam exam = examRepository.findById(examId)
                        .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado con ID: " + examId));

                LABOrdenDetalle detalle = LABOrdenDetalle.builder()
                        .exam(exam)
                        .precio(BigDecimal.ZERO) // Debería venir del catálogo de precios
                        .build();

                orden.addDetalle(detalle);
                total = total.add(detalle.getPrecio());
            }
        }

        orden.setTotal(total);

        // Guardar orden
        LABOrden ordenGuardada = ordenRepository.save(orden);

        log.info("Usuario {} creó orden {} para paciente {}",
                usuarioActual.getLogin(), ordenGuardada.getNumeroOrden(), patient.getDni());

        return convertirADTO(ordenGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenResponseDTO obtenerOrdenPorId(Long id) {
        LABOrden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));

        DatosPersonales usuarioActual = obtenerUsuarioActual();

        // Aplicar control de acceso
        if (esMedico(usuarioActual) && orden.getMedico() != null
                && !orden.getMedico().getId().equals(usuarioActual.getId())) {
            throw new SecurityException("No tiene permiso para ver esta orden");
        }

        return convertirADTO(orden);
    }

    @Override
    @Transactional
    public OrdenResponseDTO actualizarEstado(Long id, String nuevoEstado) {
        LABOrden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));

        DatosPersonales usuarioActual = obtenerUsuarioActual();

        orden.setEstado(LABOrden.EstadoOrden.valueOf(nuevoEstado));
        LABOrden ordenActualizada = ordenRepository.save(orden);

        log.info("Usuario {} actualizó estado de orden {} a {}",
                usuarioActual.getLogin(), id, nuevoEstado);

        return convertirADTO(ordenActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenResponseDTO> obtenerTodasLasOrdenes() {
        DatosPersonales usuarioActual = obtenerUsuarioActual();

        List<LABOrden> ordenes;

        if (esMedico(usuarioActual)) {
            // Médicos solo ven sus órdenes
            ordenes = ordenRepository.findByMedicoId(usuarioActual.getId());
        } else {
            // Otros roles ven todas
            ordenes = ordenRepository.findAll();
        }

        return ordenes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ========================================
    // Métodos auxiliares
    // ========================================

    private DatosPersonales obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return datosPersonalesRepository.findByLogin(username)
                .orElseThrow(() -> new SecurityException("Usuario no autenticado"));
    }

    private boolean esMedico(DatosPersonales usuario) {
        return usuario.hasRole("MEDICO");
    }

    private boolean esTecnologoOBiologo(DatosPersonales usuario) {
        return usuario.hasRole("TECNOLOGO_MEDICO") || usuario.hasRole("BIOLOGO");
    }

    private String generarNumeroOrden() {
        // Formato: ORD-YYYY-00001
        Year currentYear = Year.now();
        LABOrden lastOrden = ordenRepository.findLastOrden().orElse(null);

        int nextNumber = 1;
        if (lastOrden != null && lastOrden.getNumeroOrden() != null) {
            String[] parts = lastOrden.getNumeroOrden().split("-");
            if (parts.length == 3) {
                try {
                    nextNumber = Integer.parseInt(parts[2]) + 1;
                } catch (NumberFormatException e) {
                    log.warn("No se pudo parsear número de orden anterior: {}", lastOrden.getNumeroOrden());
                }
            }
        }

        return String.format("ORD-%d-%05d", currentYear.getValue(), nextNumber);
    }

    private OrdenResponseDTO convertirADTO(LABOrden orden) {
        List<OrdenResponseDTO.OrdenDetalleDTO> detallesDTO = orden.getDetalles().stream()
                .map(this::convertirDetalleADTO)
                .collect(Collectors.toList());

        return OrdenResponseDTO.builder()
                .id(orden.getId())
                .numeroOrden(orden.getNumeroOrden())
                .fechaOrden(orden.getFechaOrden())
                .diagnostico(orden.getDiagnostico())
                .estado(orden.getEstado().name())
                .prioridad(orden.getPrioridad().name())
                .tipoAtencion(orden.getTipoAtencion())
                .tipoMuestra(orden.getTipoMuestra())
                .observaciones(orden.getObservaciones())
                .total(orden.getTotal())
                .fechaTomaMuestra(orden.getFechaTomaMuestra())
                .fechaProcesamiento(orden.getFechaProcesamiento())
                .fechaValidacion(orden.getFechaValidacion())
                .fechaEntrega(orden.getFechaEntrega())
                .patientId(orden.getPatient().getId())
                .patientFirstName(orden.getPatient().getFirstName())
                .patientLastName(orden.getPatient().getLastName())
                .patientDni(orden.getPatient().getDni())
                .medicoId(orden.getMedico() != null ? orden.getMedico().getId() : null)
                .medicoNombre(orden.getMedico() != null ? orden.getMedico().getNombre() : null)
                .medicoFullName(orden.getMedico() != null ? orden.getMedico().getFullName() : null)
                .userId(orden.getUser() != null ? orden.getUser().getId() : null)
                .userName(orden.getUser() != null ? orden.getUser().getFullName() : null)
                .validadoPorId(orden.getValidadoPor() != null ? orden.getValidadoPor().getId() : null)
                .validadoPorName(orden.getValidadoPor() != null ? orden.getValidadoPor().getFullName() : null)
                .detalles(detallesDTO)
                .createdAt(orden.getCreatedAt())
                .updatedAt(orden.getUpdatedAt())
                .createdByName(orden.getCreatedBy() != null ? orden.getCreatedBy().getFullName() : null)
                .build();
    }

    private OrdenResponseDTO.OrdenDetalleDTO convertirDetalleADTO(LABOrdenDetalle detalle) {
        return OrdenResponseDTO.OrdenDetalleDTO.builder()
                .id(detalle.getId())
                .examId(detalle.getExam().getId())
                .examName(detalle.getExam().getNombre())
                .equipoId(detalle.getEquipoId())
                .estado(detalle.getEstado() != null ? detalle.getEstado().name() : null)
                .resultado(detalle.getResultado())
                .valorReferencia(detalle.getValorReferencia())
                .unidad(detalle.getUnidad())
                .observaciones(detalle.getObservaciones())
                .precio(detalle.getPrecio())
                .valorCritico(detalle.getValorCritico())
                .fueraRango(detalle.getFueraRango())
                .validadoPrimario(detalle.getValidadoPrimario())
                .validadoFinal(detalle.getValidadoFinal())
                .tecnologoName(detalle.getTecnologo() != null ? detalle.getTecnologo().getFullName() : null)
                .biologoName(detalle.getBiologo() != null ? detalle.getBiologo().getFullName() : null)
                .procesadoPorName(detalle.getProcesadoPor() != null ? detalle.getProcesadoPor().getFullName() : null)
                .validadoPorName(detalle.getValidadoPor() != null ? detalle.getValidadoPor().getFullName() : null)
                .fechaProcesamiento(detalle.getFechaProcesamiento())
                .fechaValidacionPrimaria(detalle.getFechaValidacionPrimaria())
                .fechaValidacionFinal(detalle.getFechaValidacionFinal())
                .createdAt(detalle.getCreatedAt())
                .updatedAt(detalle.getUpdatedAt())
                .build();
    }
}
