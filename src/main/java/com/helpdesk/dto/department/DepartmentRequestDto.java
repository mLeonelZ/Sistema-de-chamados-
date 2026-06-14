package com.helpdesk.dto.department;

import com.helpdesk.model.enums.DepartmentStatus;

import java.util.UUID;

public record DepartmentRequestDto(
        UUID id,
        String name,
        String managerName,
        DepartmentStatus status
) {
}
