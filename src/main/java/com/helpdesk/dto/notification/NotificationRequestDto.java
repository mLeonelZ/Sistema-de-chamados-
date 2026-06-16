package com.helpdesk.dto.notification;

import com.helpdesk.model.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record NotificationRequestDto(
        @NotNull(message = "O ID do usuário é obrigatório")
        UUID userId,
        
        UUID ticketId,
        
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 200, message = "O título deve ter no máximo 200 caracteres")
        String title,
        
        @NotBlank(message = "A mensagem é obrigatória")
        String message,
        
        @NotNull(message = "O tipo da notificação é obrigatório")
        NotificationType type,
        
        Boolean read
) {
}
