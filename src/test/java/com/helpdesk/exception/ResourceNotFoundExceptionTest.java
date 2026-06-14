package com.helpdesk.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceNotFoundExceptionTest {

    @Test
    void shouldStoreMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("nao encontrado");
        assertEquals("nao encontrado", ex.getMessage());
    }
}
