package com.helpdesk.dto.mapper;

import com.helpdesk.dto.user.UserRequestDto;
import com.helpdesk.dto.user.UserResponseDto;
import com.helpdesk.model.Department;
import com.helpdesk.model.User;
import com.helpdesk.model.enums.Role;
import com.helpdesk.model.enums.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserMapperTest {

    @Test
    void toEntityShouldMapFields() {
        Department department = new Department();
        department.setId(UUID.randomUUID());
        UserRequestDto dto = new UserRequestDto("Ana", "ana@a.com", "12345678", Role.CLIENTE, UserStatus.ACTIVE, department.getId());
        User user = UserMapper.toEntity(dto, department);
        assertNotNull(user.getId());
        assertEquals("Ana", user.getName());
        assertEquals("ana@a.com", user.getEmail());
        assertEquals("12345678", user.getPassword());
        assertEquals(department, user.getDepartment());
    }

    @Test
    void toResponseDtoShouldMapFields() {
        Department department = new Department();
        UUID departmentId = UUID.randomUUID();
        department.setId(departmentId);
        User user = new User();
        UUID id = UUID.randomUUID();
        user.setId(id);
        user.setName("Bob");
        user.setEmail("bob@a.com");
        user.setRole(Role.ATENDENTE);
        user.setStatus(UserStatus.INACTIVE);
        user.setDepartment(department);
        user.setCreatedAt(LocalDateTime.now().minusDays(1));
        user.setUpdatedAt(LocalDateTime.now());
        UserResponseDto response = UserMapper.toResponse(user);
        assertEquals(id, response.id());
        assertEquals("Bob", response.name());
        assertEquals(departmentId, response.departmentId());
    }
}
