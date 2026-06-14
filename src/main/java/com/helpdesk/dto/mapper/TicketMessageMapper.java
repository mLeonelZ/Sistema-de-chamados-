package com.helpdesk.dto.mapper;

import com.helpdesk.dto.ticketmessage.TicketMessageRequestDto;
import com.helpdesk.dto.ticketmessage.TicketMessageResponseDto;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.TicketMessage;
import com.helpdesk.model.User;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

public final class TicketMessageMapper {

    private TicketMessageMapper() {
    }

    public static TicketMessage toEntity(TicketMessageRequestDto dto, Ticket ticket, User author) {
        TicketMessage ticketMessage = new TicketMessage();
        write(ticketMessage, "id", UUID.randomUUID());
        write(ticketMessage, "ticket", ticket);
        write(ticketMessage, "author", author);
        write(ticketMessage, "type", dto.type());
        write(ticketMessage, "text", dto.text());
        write(ticketMessage, "createdAt", LocalDateTime.now());
        return ticketMessage;
    }

    public static TicketMessageResponseDto toResponseDto(TicketMessage ticketMessage) {
        Ticket ticket = read(ticketMessage, "ticket", Ticket.class);
        User author = read(ticketMessage, "author", User.class);
        return new TicketMessageResponseDto(
                read(ticketMessage, "id", UUID.class),
                ticket == null ? null : read(ticket, "id", UUID.class),
                author == null ? null : read(author, "id", UUID.class),
                read(ticketMessage, "type", com.helpdesk.model.enums.MessageType.class),
                read(ticketMessage, "text", String.class),
                read(ticketMessage, "createdAt", LocalDateTime.class)
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
