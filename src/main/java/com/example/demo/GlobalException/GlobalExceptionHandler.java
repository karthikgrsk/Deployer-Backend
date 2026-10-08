package com.example.demo.GlobalException;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice 
public class GlobalExceptionHandler {
    

    @ExceptionHandler (MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex){

        String message = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .findFirst()
        .map(fieldError -> fieldError.getDefaultMessage())
        .orElse("Validation failed");

        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        return ResponseEntity.badRequest().body(body);
    }
}
