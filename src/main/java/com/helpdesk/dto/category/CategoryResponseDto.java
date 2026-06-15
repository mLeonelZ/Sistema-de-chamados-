package com.helpdesk.dto.category;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CategoryResponseDto(
        UUID id,
        String name,
        UUID slaPolicyId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
