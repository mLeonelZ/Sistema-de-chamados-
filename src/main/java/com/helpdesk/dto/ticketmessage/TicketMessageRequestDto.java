package com.helpdesk.dto.ticketmessage;

import com.helpdesk.model.enums.MessageType;

import java.util.UUID;

public record TicketMessageRequestDto(
        UUID id,
        UUID ticketId,
        UUID authorId,
        MessageType type,
        String text
) {
}
