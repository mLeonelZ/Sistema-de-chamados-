package com.helpdesk.dto.ticket;

import com.helpdesk.model.enums.TicketChannel;
import com.helpdesk.model.enums.TicketPriority;
import com.helpdesk.model.enums.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketRequestDto(
        String code,
        
        @NotBlank(message = "O assunto é obrigatório")
        @Size(max = 200, message = "O assunto deve ter no máximo 200 caracteres")
        String subject,
        
        @NotBlank(message = "A descrição é obrigatória")
        String description,
        
        @NotNull(message = "A categoria é obrigatória")
        UUID categoryId,
        
        @NotNull(message = "A prioridade é obrigatória")
        TicketPriority priority,
        
        TicketStatus status,
        
        @NotNull(message = "O canal de entrada é obrigatório")
        TicketChannel channel,
        
        @NotNull(message = "O cliente é obrigatório")
        UUID clientId,
        
        UUID assigneeId,
        
        LocalDateTime slaFirstResponseDeadline,
        LocalDateTime slaResolutionDeadline,
        LocalDateTime firstResponseAt,
        LocalDateTime resolvedAt,
        LocalDateTime closedAt
) {
}
