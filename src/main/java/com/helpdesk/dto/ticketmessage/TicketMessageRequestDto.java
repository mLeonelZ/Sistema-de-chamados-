package com.helpdesk.dto.ticketmessage;

import com.helpdesk.model.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TicketMessageRequestDto(
        @NotNull(message = "O ID do chamado é obrigatório")
        UUID ticketId,
        
        @NotNull(message = "O ID do autor é obrigatório")
        UUID authorId,
        
        @NotNull(message = "O tipo da mensagem é obrigatório")
        MessageType type,
        
        @NotBlank(message = "O texto da mensagem não pode estar vazio")
        String text
) {
}
