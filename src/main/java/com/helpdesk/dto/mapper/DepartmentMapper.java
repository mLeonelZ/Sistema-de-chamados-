package com.helpdesk.dto.mapper;

import com.helpdesk.dto.department.DepartmentRequestDto;
import com.helpdesk.dto.department.DepartmentResponseDto;
import com.helpdesk.model.Department;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class DepartmentMapper {

    public static Department toEntity(DepartmentRequestDto dto) {
        Department department = new Department();
        department.setId(UUID.randomUUID());
        department.setName(dto.name());
        department.setManagerName(dto.managerName());
        department.setStatus(dto.status());
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());
        return department;
    }

    public static DepartmentResponseDto toResponse(Department department) {
        return DepartmentResponseDto.builder()
                .id(department.getId())
                .name(department.getName())
                .managerName(department.getManagerName())
                .status(department.getStatus())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }
}
