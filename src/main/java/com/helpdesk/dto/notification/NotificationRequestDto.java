package com.helpdesk.dto.notification;

import com.helpdesk.model.enums.NotificationType;

import java.util.UUID;

public record NotificationRequestDto(
        UUID id,
        UUID userId,
        UUID ticketId,
        String title,
        String message,
        NotificationType type,
        Boolean read
) {
}
