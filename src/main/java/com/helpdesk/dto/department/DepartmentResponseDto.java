package com.helpdesk.dto.department;

import com.helpdesk.model.enums.DepartmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record DepartmentResponseDto(
        UUID id,
        String name,
        String managerName,
        DepartmentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
