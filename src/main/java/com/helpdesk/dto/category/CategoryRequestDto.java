package com.helpdesk.dto.category;

import java.util.UUID;

public record CategoryRequestDto(
        String name,
        UUID slaPolicyId
) {
}
