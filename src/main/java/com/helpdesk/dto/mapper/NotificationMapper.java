package com.helpdesk.dto.mapper;

import com.helpdesk.dto.notification.NotificationRequestDto;
import com.helpdesk.dto.notification.NotificationResponseDto;
import com.helpdesk.model.Notification;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class NotificationMapper {

    public static Notification toEntity(NotificationRequestDto dto, User user, Ticket ticket) {
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setUser(user);
        notification.setTicket(ticket);
        notification.setTitle(dto.title());
        notification.setMessage(dto.message());
        notification.setType(dto.type());
        notification.setRead(dto.read());
        notification.setCreatedAt(LocalDateTime.now());
        return notification;
    }

    public static NotificationResponseDto toResponse(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .userId(notification.getUser() != null ? notification.getUser().getId() : null)
                .ticketId(notification.getTicket() != null ? notification.getTicket().getId() : null)
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .read(notification.getRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
