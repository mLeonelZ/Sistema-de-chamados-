package com.helpdesk.dto.mapper;

import com.helpdesk.dto.user.UserRequestDto;
import com.helpdesk.dto.user.UserResponseDto;
import com.helpdesk.model.Department;
import com.helpdesk.model.User;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

public final class UserMapper {

    private UserMapper() {
    }

    public static User toEntity(UserRequestDto dto, Department department) {
        User user = new User();
        write(user, "id", UUID.randomUUID());
        write(user, "name", dto.name());
        write(user, "email", dto.email());
        write(user, "password", dto.password());
        write(user, "role", dto.role());
        write(user, "status", dto.status());
        write(user, "department", department);
        write(user, "createdAt", LocalDateTime.now());
        write(user, "updatedAt", LocalDateTime.now());
        return user;
    }

    public static UserResponseDto toResponseDto(User user) {
        Department department = read(user, "department", Department.class);
        return new UserResponseDto(
                read(user, "id", UUID.class),
                read(user, "name", String.class),
                read(user, "email", String.class),
                read(user, "role", com.helpdesk.model.enums.Role.class),
                read(user, "status", com.helpdesk.model.enums.UserStatus.class),
                department == null ? null : read(department, "id", UUID.class),
                read(user, "createdAt", LocalDateTime.class),
                read(user, "updatedAt", LocalDateTime.class)
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
