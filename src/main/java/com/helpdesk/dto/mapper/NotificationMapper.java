package com.helpdesk.dto.mapper;

import com.helpdesk.dto.notification.NotificationRequestDto;
import com.helpdesk.dto.notification.NotificationResponseDto;
import com.helpdesk.model.Notification;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

public final class NotificationMapper {

    private NotificationMapper() {
    }

    public static Notification toEntity(NotificationRequestDto dto, User user, Ticket ticket) {
        Notification notification = new Notification();
        write(notification, "id", UUID.randomUUID());
        write(notification, "user", user);
        write(notification, "ticket", ticket);
        write(notification, "title", dto.title());
        write(notification, "message", dto.message());
        write(notification, "type", dto.type());
        write(notification, "read", dto.read());
        write(notification, "createdAt", LocalDateTime.now());
        return notification;
    }

    public static NotificationResponseDto toResponseDto(Notification notification) {
        User user = read(notification, "user", User.class);
        Ticket ticket = read(notification, "ticket", Ticket.class);
        return new NotificationResponseDto(
                read(notification, "id", UUID.class),
                user == null ? null : read(user, "id", UUID.class),
                ticket == null ? null : read(ticket, "id", UUID.class),
                read(notification, "title", String.class),
                read(notification, "message", String.class),
                read(notification, "type", com.helpdesk.model.enums.NotificationType.class),
                read(notification, "read", Boolean.class),
                read(notification, "createdAt", LocalDateTime.class)
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
