package com.helpdesk.dto.notification;

import com.helpdesk.model.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponseDto(
        UUID id,
        UUID userId,
        UUID ticketId,
        String title,
        String message,
        NotificationType type,
        Boolean read,
        LocalDateTime createdAt
) {
}
