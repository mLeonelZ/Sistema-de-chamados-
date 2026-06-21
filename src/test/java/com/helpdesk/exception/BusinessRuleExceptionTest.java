package com.helpdesk.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BusinessRuleExceptionTest {

    @Test
    void shouldStoreMessage() {
        BusinessRuleException ex = new BusinessRuleException("erro");
        assertEquals("erro", ex.getMessage());
    }
}
