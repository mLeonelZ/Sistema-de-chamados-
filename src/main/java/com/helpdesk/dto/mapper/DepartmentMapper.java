package com.helpdesk.dto.mapper;

import com.helpdesk.dto.department.DepartmentRequestDto;
import com.helpdesk.dto.department.DepartmentResponseDto;
import com.helpdesk.model.Department;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

public final class DepartmentMapper {

    private DepartmentMapper() {
    }

    public static Department toEntity(DepartmentRequestDto dto) {
        Department department = new Department();
        write(department, "id", dto.id());
        write(department, "name", dto.name());
        write(department, "managerName", dto.managerName());
        write(department, "status", dto.status());
        write(department, "createdAt", LocalDateTime.now());
        write(department, "updatedAt", LocalDateTime.now());
        return department;
    }

    public static DepartmentResponseDto toResponseDto(Department department) {
        return new DepartmentResponseDto(
                read(department, "id", UUID.class),
                read(department, "name", String.class),
                read(department, "managerName", String.class),
                read(department, "status", com.helpdesk.model.enums.DepartmentStatus.class),
                read(department, "createdAt", LocalDateTime.class),
                read(department, "updatedAt", LocalDateTime.class)
        );
    }

    private static <T> T read(Object source, String fieldName, Class<T> targetType) {
        try {
            Field field = findField(source.getClass(), fieldName);
            field.setAccessible(true);
            return targetType.cast(field.get(source));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static void write(Object target, String fieldName, Object value) {
        try {
            Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static Field findField(Class<?> type, String fieldName) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }
}
