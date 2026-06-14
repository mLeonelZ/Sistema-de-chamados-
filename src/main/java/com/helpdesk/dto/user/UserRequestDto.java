package com.helpdesk.dto.user;

import com.helpdesk.model.enums.Role;
import com.helpdesk.model.enums.UserStatus;

import java.util.UUID;

public record UserRequestDto(
        UUID id,
        String name,
        String email,
        String passwordHash,
        Role role,
        UserStatus status,
        UUID departmentId
) {
}
