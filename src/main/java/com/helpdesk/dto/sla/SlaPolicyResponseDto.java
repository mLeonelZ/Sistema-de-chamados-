package com.helpdesk.dto.sla;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record SlaPolicyResponseDto(
        UUID id,
        String name,
        Integer responseTimeMinutes,
        Integer resolutionTimeMinutes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
