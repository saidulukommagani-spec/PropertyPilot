package com.propertypilot.web.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void handleDuplicateResourceException() {

        ResponseEntity<Map<String,Object>> response =
                handler.handleDuplicateResourceException(
                        new DuplicateResourceException(
                                "Duplicate"));

        assertEquals(
                409,
                response.getStatusCode().value());
    }

    @Test
    void handleResourceNotFoundException() {

        ResponseEntity<Map<String,Object>> response =
                handler.handleResourceNotFoundException(
                        new ResourceNotFoundException(
                                "Not Found"));

        assertEquals(
                404,
                response.getStatusCode().value());
    }

    @Test
    void handleGenericException() {

        ResponseEntity<Map<String,Object>> response =
                handler.handleException(
                        new RuntimeException(
                                "Error"));

        assertEquals(
                500,
                response.getStatusCode().value());
    }

    @Test
    void handleValidationException() {

        BeanPropertyBindingResult binding =
                new BeanPropertyBindingResult(
                        new Object(),
                        "object");

        binding.addError(
                new FieldError(
                        "object",
                        "email",
                        "Invalid email"));

        MethodArgumentNotValidException ex =
                mock(MethodArgumentNotValidException.class);

        when(ex.getBindingResult())
                .thenReturn(binding);

        ResponseEntity<Map<String,Object>> response =
                handler.handleValidationException(ex);

        assertEquals(
                400,
                response.getStatusCode().value());
    }
}