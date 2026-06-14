package com.helpdesk.dto.ticket;

import com.helpdesk.model.enums.TicketChannel;
import com.helpdesk.model.enums.TicketPriority;
import com.helpdesk.model.enums.TicketStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketRequestDto(
        String code,
        String subject,
        String description,
        UUID categoryId,
        TicketPriority priority,
        TicketStatus status,
        TicketChannel channel,
        UUID clientId,
        UUID assigneeId,
        LocalDateTime slaFirstResponseDeadline,
        LocalDateTime slaResolutionDeadline,
        LocalDateTime firstResponseAt,
        LocalDateTime resolvedAt,
        LocalDateTime closedAt
) {
}
