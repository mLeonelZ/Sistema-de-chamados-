package com.helpdesk.dto.user;

import com.helpdesk.model.enums.Role;
import com.helpdesk.model.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String name,
        String email,
        Role role,
        UserStatus status,
        UUID departmentId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
