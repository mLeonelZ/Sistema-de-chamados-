package com.helpdesk.dto.mapper;

import com.helpdesk.dto.ticket.TicketRequestDto;
import com.helpdesk.dto.ticket.TicketResponseDto;
import com.helpdesk.model.Category;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

public final class TicketMapper {

    private TicketMapper() {
    }

    public static Ticket toEntity(TicketRequestDto dto, Category category, User client, User assignee) {
        Ticket ticket = new Ticket();
        write(ticket, "id", UUID.randomUUID());
        write(ticket, "code", dto.code());
        write(ticket, "subject", dto.subject());
        write(ticket, "description", dto.description());
        write(ticket, "category", category);
        write(ticket, "priority", dto.priority());
        write(ticket, "status", dto.status());
        write(ticket, "channel", dto.channel());
        write(ticket, "client", client);
        write(ticket, "assignee", assignee);
        write(ticket, "slaFirstResponseDeadline", dto.slaFirstResponseDeadline());
        write(ticket, "slaResolutionDeadline", dto.slaResolutionDeadline());
        write(ticket, "firstResponseAt", dto.firstResponseAt());
        write(ticket, "resolvedAt", dto.resolvedAt());
        write(ticket, "closedAt", dto.closedAt());
        write(ticket, "createdAt", LocalDateTime.now());
        write(ticket, "updatedAt", LocalDateTime.now());
        return ticket;
    }

    public static TicketResponseDto toResponseDto(Ticket ticket) {
        Category category = read(ticket, "category", Category.class);
        User client = read(ticket, "client", User.class);
        User assignee = read(ticket, "assignee", User.class);
        return new TicketResponseDto(
                read(ticket, "id", UUID.class),
                read(ticket, "code", String.class),
                read(ticket, "subject", String.class),
                read(ticket, "description", String.class),
                category == null ? null : read(category, "id", UUID.class),
                read(ticket, "priority", com.helpdesk.model.enums.TicketPriority.class),
                read(ticket, "status", com.helpdesk.model.enums.TicketStatus.class),
                read(ticket, "channel", com.helpdesk.model.enums.TicketChannel.class),
                client == null ? null : read(client, "id", UUID.class),
                assignee == null ? null : read(assignee, "id", UUID.class),
                read(ticket, "slaFirstResponseDeadline", LocalDateTime.class),
                read(ticket, "slaResolutionDeadline", LocalDateTime.class),
                read(ticket, "firstResponseAt", LocalDateTime.class),
                read(ticket, "resolvedAt", LocalDateTime.class),
                read(ticket, "closedAt", LocalDateTime.class),
                read(ticket, "createdAt", LocalDateTime.class),
                read(ticket, "updatedAt", LocalDateTime.class)
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
