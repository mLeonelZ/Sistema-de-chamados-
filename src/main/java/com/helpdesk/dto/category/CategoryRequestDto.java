package com.helpdesk.dto.category;

import java.util.UUID;

public record CategoryRequestDto(
        UUID id,
        String name,
        UUID slaPolicyId
) {
}
