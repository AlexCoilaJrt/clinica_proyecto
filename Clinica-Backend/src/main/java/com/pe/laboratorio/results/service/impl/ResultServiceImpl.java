package com.pe.laboratorio.results.service.impl;

import com.pe.laboratorio.exception.ResourceNotFoundException;
import com.pe.laboratorio.exception.ValidationException;
import com.pe.laboratorio.orders.entity.OrderExam;
import com.pe.laboratorio.orders.repository.OrderExamRepository;
import com.pe.laboratorio.results.dto.CreateResultRequest;
import com.pe.laboratorio.results.dto.ExamResultDTO;
import com.pe.laboratorio.results.dto.ValidateResultRequest;
import com.pe.laboratorio.results.entity.ExamResult;
import com.pe.laboratorio.results.entity.ValidationStatus;
import com.pe.laboratorio.results.repository.ExamResultRepository;
import com.pe.laboratorio.results.service.ResultService;
import com.pe.laboratorio.users.entity.User;
import com.pe.laboratorio.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultServiceImpl implements ResultService {

    private final ExamResultRepository resultRepository;
    private final OrderExamRepository orderExamRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ExamResultDTO createResult(CreateResultRequest request, String username) {
        OrderExam orderExam = orderExamRepository.findById(request.getOrderExamId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Examen de orden no encontrado con ID: " + request.getOrderExamId()));

        if (resultRepository.findByOrderExamId(request.getOrderExamId()).isPresent()) {
            throw new ValidationException("Ya existe un resultado para este examen de orden");
        }

        User enteredBy = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        ExamResult result = ExamResult.builder()
                .orderExam(orderExam)
                .value(request.getValue())
                .unit(request.getUnit())
                .referenceRange(request.getReferenceRange())
                .validationStatus(ValidationStatus.PENDIENTE)
                .enteredBy(enteredBy)
                .observations(request.getObservations())
                .build();

        result = resultRepository.save(result);
        return mapToDTO(result);
    }

    @Override
    public ExamResultDTO getResultById(Long id) {
        ExamResult result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultado no encontrado con ID: " + id));
        return mapToDTO(result);
    }

    @Override
    public List<ExamResultDTO> getResultsByOrder(Long orderId) {
        return resultRepository.findByOrderId(orderId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamResultDTO> getPendingValidation() {
        return resultRepository.findPendingValidation().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExamResultDTO validatePrimary(Long id, ValidateResultRequest request, String username) {
        ExamResult result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultado no encontrado con ID: " + id));

        if (result.getValidationStatus() != ValidationStatus.PENDIENTE) {
            throw new ValidationException("El resultado ya ha sido validado");
        }

        User validator = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        result.setValidationStatus(ValidationStatus.VALIDADO_PRIMARIO);
        result.setPrimaryValidator(validator);
        result.setPrimaryValidationDate(LocalDateTime.now());

        if (request.getObservations() != null && !request.getObservations().isEmpty()) {
            String currentObs = result.getObservations() != null ? result.getObservations() : "";
            result.setObservations(currentObs + "\n[Validación Primaria] " + request.getObservations());
        }

        result = resultRepository.save(result);
        return mapToDTO(result);
    }

    @Override
    @Transactional
    public ExamResultDTO validateFinal(Long id, ValidateResultRequest request, String username) {
        ExamResult result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultado no encontrado con ID: " + id));

        if (result.getValidationStatus() != ValidationStatus.VALIDADO_PRIMARIO) {
            throw new ValidationException("El resultado debe tener validación primaria antes de la validación final");
        }

        User validator = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        result.setValidationStatus(ValidationStatus.VALIDADO_FINAL);
        result.setFinalValidator(validator);
        result.setFinalValidationDate(LocalDateTime.now());

        if (request.getObservations() != null && !request.getObservations().isEmpty()) {
            String currentObs = result.getObservations() != null ? result.getObservations() : "";
            result.setObservations(currentObs + "\n[Validación Final] " + request.getObservations());
        }

        result = resultRepository.save(result);
        return mapToDTO(result);
    }

    @Override
    @Transactional
    public void deleteResult(Long id) {
        if (!resultRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resultado no encontrado con ID: " + id);
        }
        resultRepository.deleteById(id);
    }

    private ExamResultDTO mapToDTO(ExamResult result) {
        String patientName = result.getOrderExam().getOrder().getPatient().getFirstName() + " " +
                result.getOrderExam().getOrder().getPatient().getLastName();

        return ExamResultDTO.builder()
                .id(result.getId())
                .orderExamId(result.getOrderExam().getId())
                .examName(result.getOrderExam().getExam().getName())
                .patientName(patientName)
                .value(result.getValue())
                .unit(result.getUnit())
                .referenceRange(result.getReferenceRange())
                .validationStatus(result.getValidationStatus())
                .enteredByName(result.getEnteredBy() != null ? result.getEnteredBy().getUsername() : null)
                .enteredDate(result.getEnteredDate())
                .primaryValidatorName(
                        result.getPrimaryValidator() != null ? result.getPrimaryValidator().getUsername() : null)
                .primaryValidationDate(result.getPrimaryValidationDate())
                .finalValidatorName(
                        result.getFinalValidator() != null ? result.getFinalValidator().getUsername() : null)
                .finalValidationDate(result.getFinalValidationDate())
                .observations(result.getObservations())
                .build();
    }
}
