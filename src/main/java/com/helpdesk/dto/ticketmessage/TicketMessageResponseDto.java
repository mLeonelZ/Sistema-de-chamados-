package com.helpdesk.dto.ticketmessage;

import com.helpdesk.model.enums.MessageType;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record TicketMessageResponseDto(
        UUID id,
        UUID ticketId,
        UUID authorId,
        MessageType type,
        String text,
        LocalDateTime createdAt
) {
}
