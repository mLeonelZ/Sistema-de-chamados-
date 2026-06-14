package com.helpdesk.dto.category;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryResponseDto(
        UUID id,
        String name,
        UUID slaPolicyId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
