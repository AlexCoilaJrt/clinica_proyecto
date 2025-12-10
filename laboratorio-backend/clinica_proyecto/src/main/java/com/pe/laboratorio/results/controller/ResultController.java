package com.pe.laboratorio.results.controller;

import com.pe.laboratorio.results.dto.CreateResultRequest;
import com.pe.laboratorio.results.dto.ExamResultDTO;
import com.pe.laboratorio.results.dto.ValidateResultRequest;
import com.pe.laboratorio.results.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    @PostMapping
    public ResponseEntity<ExamResultDTO> createResult(
            @RequestBody CreateResultRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        ExamResultDTO created = resultService.createResult(request, username);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamResultDTO> getResultById(@PathVariable Long id) {
        ExamResultDTO result = resultService.getResultById(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<ExamResultDTO>> getResultsByOrder(@PathVariable Long orderId) {
        List<ExamResultDTO> results = resultService.getResultsByOrder(orderId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/pending-validation")
    public ResponseEntity<List<ExamResultDTO>> getPendingValidation() {
        List<ExamResultDTO> results = resultService.getPendingValidation();
        return ResponseEntity.ok(results);
    }

    @PutMapping("/{id}/validate-primary")
    public ResponseEntity<ExamResultDTO> validatePrimary(
            @PathVariable Long id,
            @RequestBody ValidateResultRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        ExamResultDTO validated = resultService.validatePrimary(id, request, username);
        return ResponseEntity.ok(validated);
    }

    @PutMapping("/{id}/validate-final")
    public ResponseEntity<ExamResultDTO> validateFinal(
            @PathVariable Long id,
            @RequestBody ValidateResultRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        ExamResultDTO validated = resultService.validateFinal(id, request, username);
        return ResponseEntity.ok(validated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        resultService.deleteResult(id);
        return ResponseEntity.noContent().build();
    }
}
