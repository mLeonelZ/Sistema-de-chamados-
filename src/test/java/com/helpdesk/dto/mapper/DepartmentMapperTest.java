package com.helpdesk.dto.mapper;

import com.helpdesk.dto.department.DepartmentRequestDto;
import com.helpdesk.dto.department.DepartmentResponseDto;
import com.helpdesk.model.Department;
import com.helpdesk.model.enums.DepartmentStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DepartmentMapperTest {

    @Test
    void toEntityShouldMapFields() {
        DepartmentRequestDto dto = new DepartmentRequestDto("N1", "Gestor", DepartmentStatus.ACTIVE);
        Department department = DepartmentMapper.toEntity(dto);
        assertNotNull(department.getId());
        assertEquals("N1", department.getName());
        assertEquals("Gestor", department.getManagerName());
        assertEquals(DepartmentStatus.ACTIVE, department.getStatus());
    }

    @Test
    void toResponseDtoShouldMapFields() {
        Department department = new Department();
        UUID id = UUID.randomUUID();
        department.setId(id);
        department.setName("N2");
        department.setManagerName("Coord");
        department.setStatus(DepartmentStatus.INACTIVE);
        department.setCreatedAt(LocalDateTime.now().minusDays(1));
        department.setUpdatedAt(LocalDateTime.now());
        DepartmentResponseDto response = DepartmentMapper.toResponse(department);
        assertEquals(id, response.id());
        assertEquals("N2", response.name());
        assertEquals(DepartmentStatus.INACTIVE, response.status());
    }
}
