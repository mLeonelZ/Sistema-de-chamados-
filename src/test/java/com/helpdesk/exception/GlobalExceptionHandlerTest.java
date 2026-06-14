package com.helpdesk.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFoundShouldReturn404() {
        ResponseEntity<Map<String, String>> response = handler.handleResourceNotFound(new ResourceNotFoundException("x"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("x", response.getBody().get("erro"));
    }

    @Test
    void handleBusinessRuleShouldReturn409() {
        ResponseEntity<Map<String, String>> response = handler.handleBusinessRule(new BusinessRuleException("y"));
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("y", response.getBody().get("erro"));
    }
}
