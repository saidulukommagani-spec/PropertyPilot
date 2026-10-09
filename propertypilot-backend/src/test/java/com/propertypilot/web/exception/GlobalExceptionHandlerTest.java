package com.propertypilot.web.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.mock.web.MockHttpServletRequest;

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
                                "Error"),
                        new MockHttpServletRequest("GET", "/api/v1/billing/invoices"));

        assertEquals(
                500,
                response.getStatusCode().value());
        assertEquals("An unexpected error occurred", response.getBody().get("message"));
    }

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    void handleGenericExceptionLogsRequestMethodAndPath(CapturedOutput output) {
        handler.handleException(
                new RuntimeException("Database mapping failed"),
                new MockHttpServletRequest("GET", "/api/v1/billing/invoices"));

        assertTrue(output.getOut().contains(
                "Unhandled request exception on GET /api/v1/billing/invoices"));
    }

    @Test
    void handleMethodNotSupportedReturns405AndAllowedMethods() {
        HttpRequestMethodNotSupportedException exception = mock(HttpRequestMethodNotSupportedException.class);
        when(exception.getSupportedHttpMethods()).thenReturn(java.util.Set.of(HttpMethod.POST));

        ResponseEntity<Map<String, Object>> response = handler.handleMethodNotSupported(exception);

        assertEquals(405, response.getStatusCode().value());
        assertEquals("HTTP method is not supported for this endpoint", response.getBody().get("message"));
        assertTrue(response.getHeaders().getAllow().contains(HttpMethod.POST));
    }

    @Test
    void handleAccessDeniedReturns403WithoutLeakingDetails() {
        ResponseEntity<Map<String, Object>> response = handler.handleAccessDenied(
                new AccessDeniedException("sensitive authorization detail"));

        assertEquals(403, response.getStatusCode().value());
        assertEquals("You do not have permission to access this resource", response.getBody().get("message"));
    }

    @Test
    void handleNoResourceFoundReturns404() {
        ResponseEntity<Map<String, Object>> response = handler.handleNoResourceFound(
                new NoResourceFoundException(HttpMethod.GET, "/api/payments"));

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Endpoint not found", response.getBody().get("message"));
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
