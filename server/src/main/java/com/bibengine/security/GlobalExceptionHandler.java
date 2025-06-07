package com.bibengine.security;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentNotValidException;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.Map;

/** Globalne obługiwanie wyjątków REST zwraca prosty komunikat w formacie JSON. */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({IllegalArgumentException.class, DataIntegrityViolationException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(Exception ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOther(Exception ex) {
        return ResponseEntity.internalServerError().body(Map.of("error", "Wewnętrzny błąd serwera"));
    }
}
