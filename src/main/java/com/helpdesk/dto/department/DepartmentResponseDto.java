package com.helpdesk.dto.department;

import com.helpdesk.model.enums.DepartmentStatus;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record DepartmentResponseDto(
        UUID id,
        String name,
        String managerName,
        DepartmentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
