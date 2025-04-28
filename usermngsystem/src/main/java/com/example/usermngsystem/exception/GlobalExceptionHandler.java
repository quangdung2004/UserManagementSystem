package com.example.usermngsystem.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 400 - Lỗi validate @Valid trong DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    // 400 - Lỗi validate từ @Validated + service hoặc controller
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    // Runtime
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error: " + ex.getMessage());
    }

    // Exception chung
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAll(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
    }
}
