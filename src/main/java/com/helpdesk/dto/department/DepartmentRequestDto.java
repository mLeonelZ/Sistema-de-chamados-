package com.helpdesk.dto.department;

import com.helpdesk.model.enums.DepartmentStatus;

public record DepartmentRequestDto(
        String name,
        String managerName,
        DepartmentStatus status
) {
}
