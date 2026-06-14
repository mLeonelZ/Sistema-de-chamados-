package com.helpdesk.dto.mapper;

import com.helpdesk.dto.sla.SlaPolicyRequestDto;
import com.helpdesk.dto.sla.SlaPolicyResponseDto;
import com.helpdesk.model.SlaPolicy;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

public final class SlaPolicyMapper {

    private SlaPolicyMapper() {
    }

    public static SlaPolicy toEntity(SlaPolicyRequestDto dto) {
        SlaPolicy slaPolicy = new SlaPolicy();
        write(slaPolicy, "id", dto.id());
        write(slaPolicy, "name", dto.name());
        write(slaPolicy, "responseTimeMinutes", dto.responseTimeMinutes());
        write(slaPolicy, "resolutionTimeMinutes", dto.resolutionTimeMinutes());
        write(slaPolicy, "createdAt", LocalDateTime.now());
        write(slaPolicy, "updatedAt", LocalDateTime.now());
        return slaPolicy;
    }

    public static SlaPolicyResponseDto toResponseDto(SlaPolicy slaPolicy) {
        return new SlaPolicyResponseDto(
                read(slaPolicy, "id", UUID.class),
                read(slaPolicy, "name", String.class),
                read(slaPolicy, "responseTimeMinutes", Integer.class),
                read(slaPolicy, "resolutionTimeMinutes", Integer.class),
                read(slaPolicy, "createdAt", LocalDateTime.class),
                read(slaPolicy, "updatedAt", LocalDateTime.class)
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
