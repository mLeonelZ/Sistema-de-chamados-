package com.helpdesk.dto.sla;

public record SlaPolicyRequestDto(
        String name,
        Integer responseTimeMinutes,
        Integer resolutionTimeMinutes
) {
}
