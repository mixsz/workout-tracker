package com.mixsz.workouttracker.exception.handler;

import com.mixsz.workouttracker.exception.custom.BusinessException;
import com.mixsz.workouttracker.exception.custom.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final List<String> FIELD_ORDER = List.of("name", "email", "password", "confirmPassword");
    private static final List<String> ANNOTATION_PRIORITY = List.of("NotBlank", "NotNull", "Size", "Email", "Pattern");

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusiness(BusinessException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().stream()
                .sorted(Comparator
                        .comparingInt((FieldError error) -> {
                            int index = FIELD_ORDER.indexOf(error.getField());
                            return index == -1 ? Integer.MAX_VALUE : index;
                        })
                        .thenComparingInt(error -> {
                            int index = ANNOTATION_PRIORITY.indexOf(error.getCode());
                            return index == -1 ? Integer.MAX_VALUE : index;
                        }))
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos.");
    }
}