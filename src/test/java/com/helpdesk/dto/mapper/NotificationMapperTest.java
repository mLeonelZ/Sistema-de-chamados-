package com.helpdesk.dto.mapper;

import com.helpdesk.dto.notification.NotificationRequestDto;
import com.helpdesk.dto.notification.NotificationResponseDto;
import com.helpdesk.model.Notification;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;
import com.helpdesk.model.enums.NotificationType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NotificationMapperTest {

    @Test
    void toEntityShouldMapFields() {
        User user = new User();
        user.setId(UUID.randomUUID());
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID());
        NotificationRequestDto dto = new NotificationRequestDto(user.getId(), ticket.getId(), "t", "m", NotificationType.INFO, false);
        Notification notification = NotificationMapper.toEntity(dto, user, ticket);
        assertNotNull(notification.getId());
        assertEquals(user, notification.getUser());
        assertEquals(ticket, notification.getTicket());
        assertEquals("t", notification.getTitle());
        assertEquals(NotificationType.INFO, notification.getType());
    }

    @Test
    void toResponseDtoShouldMapFields() {
        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        Ticket ticket = new Ticket();
        UUID ticketId = UUID.randomUUID();
        ticket.setId(ticketId);
        Notification notification = new Notification();
        UUID id = UUID.randomUUID();
        notification.setId(id);
        notification.setUser(user);
        notification.setTicket(ticket);
        notification.setTitle("x");
        notification.setMessage("y");
        notification.setType(NotificationType.SUCCESS);
        notification.setRead(true);
        notification.setCreatedAt(LocalDateTime.now());
        NotificationResponseDto response = NotificationMapper.toResponse(notification);
        assertEquals(id, response.id());
        assertEquals(userId, response.userId());
        assertEquals(ticketId, response.ticketId());
    }
}
