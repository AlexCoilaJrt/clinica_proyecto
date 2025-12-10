package com.pe.laboratorio.exception;

import com.pe.laboratorio.auth.service.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AuthException.class)
  public ResponseEntity<ErrorResponse> handleAuthException(AuthException ex) {
    ErrorResponse errorResponse = ErrorResponse.builder()
        .success(false)
        .message(ex.getMessage())
        .remainingAttempts(ex.getRemainingAttempts())
        .blocked(ex.isBlocked())
        .unblockTime(ex.getUnblockTime())
        .timestamp(LocalDateTime.now())
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("message", "Ocurrió un error inesperado en el servidor.");
    errorDetails.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    errorDetails.put("error", "Error Interno del Servidor");
    return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}