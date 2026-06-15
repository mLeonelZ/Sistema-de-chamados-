package com.helpdesk.dto.notification;

import com.helpdesk.model.enums.NotificationType;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
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
