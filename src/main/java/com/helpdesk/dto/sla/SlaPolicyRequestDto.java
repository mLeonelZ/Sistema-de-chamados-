package com.helpdesk.dto.sla;

import java.util.UUID;

public record SlaPolicyRequestDto(
        UUID id,
        String name,
        Integer responseTimeMinutes,
        Integer resolutionTimeMinutes
) {
}
