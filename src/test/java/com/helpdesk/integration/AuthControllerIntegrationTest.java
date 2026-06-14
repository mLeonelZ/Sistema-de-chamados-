package com.helpdesk.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helpdesk.dto.auth.AuthLoginRequestDto;
import com.helpdesk.model.Department;
import com.helpdesk.model.User;
import com.helpdesk.model.enums.Role;
import com.helpdesk.model.enums.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginShouldReturnTokenWhenCredentialsAreValid() throws Exception {
        UUID userId = UUID.randomUUID();
        Department department = new Department();
        department.setId(UUID.randomUUID());
        department.setName("Suporte");
        department.setManagerName("Manager");
        department.setStatus(com.helpdesk.model.enums.DepartmentStatus.ACTIVE);
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());

        User user = new User();
        user.setId(userId);
        user.setName("Admin");
        user.setEmail("admin@helpdesk.local");
        user.setPassword(new BCryptPasswordEncoder().encode("12345678"));
        user.setRole(Role.ADMIN);
        user.setStatus(UserStatus.ACTIVE);
        user.setDepartment(department);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findByEmail("admin@helpdesk.local")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthLoginRequestDto("admin@helpdesk.local", "12345678"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.email").value("admin@helpdesk.local"));
    }

    @Test
    void loginShouldReturnUnauthorizedWhenPasswordIsInvalid() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Admin");
        user.setEmail("admin@helpdesk.local");
        user.setPassword(new BCryptPasswordEncoder().encode("12345678"));
        user.setRole(Role.ADMIN);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findByEmail("admin@helpdesk.local")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthLoginRequestDto("admin@helpdesk.local", "senha-incorreta"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro").value("Credenciais inválidas."));
    }
}
