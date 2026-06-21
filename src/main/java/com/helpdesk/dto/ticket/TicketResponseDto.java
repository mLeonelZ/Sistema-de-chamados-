package com.helpdesk.dto.ticket;

import com.helpdesk.model.enums.TicketChannel;
import com.helpdesk.model.enums.TicketPriority;
import com.helpdesk.model.enums.TicketStatus;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record TicketResponseDto(
        UUID id,
        String code,
        String subject,
        String description,
        UUID categoryId,
        String category,
        TicketPriority priority,
        TicketStatus status,
        TicketChannel channel,
        UUID clientId,
        String clientName,
        UUID assigneeId,
        String assigneeName,
        LocalDateTime slaFirstResponseDeadline,
        LocalDateTime slaResolutionDeadline,
        LocalDateTime firstResponseAt,
        LocalDateTime resolvedAt,
        LocalDateTime closedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime slaDeadline,
        String slaFirstResponse,
        String slaResolution
) {
}
