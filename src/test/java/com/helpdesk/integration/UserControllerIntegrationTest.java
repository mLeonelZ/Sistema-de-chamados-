package com.helpdesk.integration;

import com.helpdesk.model.Department;
import com.helpdesk.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void findAllShouldReturn200() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Maria");
        user.setEmail("maria@helpdesk.com");
        when(userRepository.findAllWithDepartment()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + generateValidToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Maria"));
    }

    @Test
    void findByIdShouldReturn200() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setName("Maria");
        user.setEmail("maria@helpdesk.com");
        when(userRepository.findByIdWithDepartment(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/users/{id}", userId)
                        .header("Authorization", "Bearer " + generateValidToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("maria@helpdesk.com"));
    }

    @Test
    void updateShouldReturn200() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();
        Department department = new Department();
        department.setId(departmentId);
        User existing = new User();
        existing.setId(userId);
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        existing.setPassword("$2a$10$abc");
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(userRepository.findByIdWithDepartment(userId)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmailAndIdNot("novo@helpdesk.com", userId)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String body = """
                {
                  "name":"Novo Nome",
                  "email":"novo@helpdesk.com",
                  "password":"12345678",
                  "role":"CLIENTE",
                  "status":"ACTIVE",
                  "departmentId":"%s"
                }
                """.formatted(departmentId);

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .header("Authorization", "Bearer " + generateValidToken())
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("novo@helpdesk.com"));
    }

    @Test
    void updateShouldReturn400WhenInvalidBody() throws Exception {
        UUID userId = UUID.randomUUID();
        String body = """
                {"name":"","email":"invalido","password":"123","role":null,"status":null,"departmentId":null}
                """;

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .header("Authorization", "Bearer " + generateValidToken())
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateShouldReturn409WhenEmailAlreadyExists() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();
        Department department = new Department();
        department.setId(departmentId);
        User existing = new User();
        existing.setId(userId);
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(userRepository.findByIdWithDepartment(userId)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmailAndIdNot("duplicado@helpdesk.com", userId)).thenReturn(true);

        String body = """
                {"name":"Novo","email":"duplicado@helpdesk.com","password":"12345678","role":"CLIENTE","status":"ACTIVE","departmentId":"%s"}
                """.formatted(departmentId);

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .header("Authorization", "Bearer " + generateValidToken())
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void updateShouldReturn404WhenUserNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();
        Department department = new Department();
        department.setId(departmentId);
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(userRepository.findByIdWithDepartment(userId)).thenReturn(Optional.empty());

        String body = """
                {"name":"Novo","email":"novo2@helpdesk.com","password":"12345678","role":"CLIENTE","status":"ACTIVE","departmentId":"%s"}
                """.formatted(departmentId);

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .header("Authorization", "Bearer " + generateValidToken())
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isNotFound());
    }
}
