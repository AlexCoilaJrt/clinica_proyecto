package com.pe.laboratorio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AuthException.class)
  public ResponseEntity<Map<String, Object>> handleAuthException(AuthException ex) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("message", ex.getMessage());
    errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
    errorDetails.put("error", "Autenticación Fallida");

    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
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