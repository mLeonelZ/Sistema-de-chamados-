package com.helpdesk.service;

import com.helpdesk.model.Department;
import com.helpdesk.model.User;
import com.helpdesk.model.enums.DepartmentStatus;
import com.helpdesk.model.enums.Role;
import com.helpdesk.model.enums.UserStatus;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtServiceTest {

    @Test
    void shouldGenerateAndParseToken() {
        JwtService jwtService = new JwtService("super-secret-change-me-super-secret-change-me", 3600);

        Department department = new Department();
        department.setId(UUID.randomUUID());
        department.setName("Suporte");
        department.setManagerName("Manager");
        department.setStatus(DepartmentStatus.ACTIVE);
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Admin");
        user.setEmail("admin@helpdesk.local");
        user.setPassword("hash");
        user.setRole(Role.ADMIN);
        user.setStatus(UserStatus.ACTIVE);
        user.setDepartment(department);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        String token = jwtService.generateToken(user);
        Claims claims = jwtService.parseToken(token);

        assertNotNull(token);
        assertEquals(user.getId().toString(), claims.getSubject());
        assertEquals(user.getEmail(), claims.get("email", String.class));
        assertEquals(user.getRole().name(), claims.get("role", String.class));
    }
}
