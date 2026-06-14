package com.helpdesk.dto.mapper;

import com.helpdesk.dto.category.CategoryRequestDto;
import com.helpdesk.dto.category.CategoryResponseDto;
import com.helpdesk.model.Category;
import com.helpdesk.model.SlaPolicy;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static Category toEntity(CategoryRequestDto dto, SlaPolicy slaPolicy) {
        Category category = new Category();
        write(category, "id", UUID.randomUUID());
        write(category, "name", dto.name());
        write(category, "slaPolicy", slaPolicy);
        write(category, "createdAt", LocalDateTime.now());
        write(category, "updatedAt", LocalDateTime.now());
        return category;
    }

    public static CategoryResponseDto toResponseDto(Category category) {
        SlaPolicy slaPolicy = read(category, "slaPolicy", SlaPolicy.class);
        return new CategoryResponseDto(
                read(category, "id", UUID.class),
                read(category, "name", String.class),
                slaPolicy == null ? null : read(slaPolicy, "id", UUID.class),
                read(category, "createdAt", LocalDateTime.class),
                read(category, "updatedAt", LocalDateTime.class)
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
