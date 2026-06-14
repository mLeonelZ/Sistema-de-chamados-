package com.helpdesk.dto.auth;

import com.helpdesk.model.enums.Role;
import com.helpdesk.model.enums.UserStatus;

import java.util.UUID;

public record AuthLoginResponseDto(
        String token,
        String tokenType,
        long expiresInSeconds,
        UUID userId,
        String name,
        String email,
        Role role,
        UserStatus status
) {
}
