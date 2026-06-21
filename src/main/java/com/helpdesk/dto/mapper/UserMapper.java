package com.helpdesk.dto.mapper;

import com.helpdesk.dto.user.UserRequestDto;
import com.helpdesk.dto.user.UserResponseDto;
import com.helpdesk.model.Department;
import com.helpdesk.model.User;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class UserMapper {

    public static User toEntity(UserRequestDto dto, Department department) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setRole(dto.role());
        user.setStatus(dto.status());
        user.setDepartment(department);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    public static UserResponseDto toResponse(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .department(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
