package com.helpdesk.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FieldMapperTest {

    static class Base {
        private String inherited = "base";
    }

    static class Child extends Base {
        private String value;
    }

    @Test
    void writeAndReadShouldWork() {
        Child child = new Child();
        FieldMapper.write(child, "value", "abc");
        assertEquals("abc", FieldMapper.read(child, "value", String.class));
    }

    @Test
    void readShouldFindInheritedField() {
        Child child = new Child();
        assertEquals("base", FieldMapper.read(child, "inherited", String.class));
    }

    @Test
    void writeShouldThrowWhenFieldDoesNotExist() {
        Child child = new Child();
        assertThrows(IllegalStateException.class, () -> FieldMapper.write(child, "missing", "x"));
    }
}
