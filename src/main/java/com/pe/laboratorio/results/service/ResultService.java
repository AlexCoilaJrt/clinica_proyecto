package com.pe.laboratorio.results.service;

import com.pe.laboratorio.results.dto.CreateResultRequest;
import com.pe.laboratorio.results.dto.ExamResultDTO;
import com.pe.laboratorio.results.dto.ValidateResultRequest;
import java.util.List;

public interface ResultService {
    ExamResultDTO createResult(CreateResultRequest request, String username);

    ExamResultDTO getResultById(Long id);

    List<ExamResultDTO> getResultsByOrder(Long orderId);

    List<ExamResultDTO> getPendingValidation();

    ExamResultDTO validatePrimary(Long id, ValidateResultRequest request, String username);

    ExamResultDTO validateFinal(Long id, ValidateResultRequest request, String username);

    void deleteResult(Long id);
}
