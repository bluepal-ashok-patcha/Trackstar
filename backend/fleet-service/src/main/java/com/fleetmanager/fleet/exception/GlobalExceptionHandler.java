package com.fleetmanager.fleet.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
 
    @ExceptionHandler(TenantContextMissingException.class)
    public ResponseEntity<Map<String, String>> handleTenantMissing(
            TenantContextMissingException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", "Unauthorized");
        error.put("message", ex.getMessage());
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", String.valueOf(HttpStatus.UNAUTHORIZED.value()));

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DuplicateLicensePlateException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateLicensePlate(
            DuplicateLicensePlateException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
