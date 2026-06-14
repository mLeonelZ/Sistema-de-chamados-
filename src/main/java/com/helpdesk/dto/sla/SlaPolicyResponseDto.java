package com.helpdesk.dto.sla;

import java.time.LocalDateTime;
import java.util.UUID;

public record SlaPolicyResponseDto(
        UUID id,
        String name,
        Integer responseTimeMinutes,
        Integer resolutionTimeMinutes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
