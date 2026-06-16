package com.helpdesk.integration;

import com.helpdesk.model.Department;
import com.helpdesk.model.enums.DepartmentStatus;
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

class DepartmentControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void findAllShouldReturn200() throws Exception {
        Department department = new Department();
        department.setId(UUID.randomUUID());
        department.setName("Suporte");
        department.setManagerName("Gestor");
        department.setStatus(DepartmentStatus.ACTIVE);
        department.setCreatedAt(LocalDateTime.now().minusDays(1));
        department.setUpdatedAt(LocalDateTime.now());
        when(departmentRepository.findAll()).thenReturn(List.of(department));

        mockMvc.perform(get("/api/v1/departments")
                        .header("Authorization", "Bearer " + generateValidToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Suporte"));
    }

    @Test
    void findByIdShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        Department department = new Department();
        department.setId(id);
        department.setName("Infra");
        department.setManagerName("Lider");
        department.setStatus(DepartmentStatus.ACTIVE);
        department.setCreatedAt(LocalDateTime.now().minusDays(1));
        department.setUpdatedAt(LocalDateTime.now());
        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));

        mockMvc.perform(get("/api/v1/departments/{id}", id)
                        .header("Authorization", "Bearer " + generateValidToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Infra"));
    }

    @Test
    void updateShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();

        Department existing = new Department();
        existing.setId(id);
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));

        when(departmentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String body = """
                {
                  "name":"Operações",
                  "managerName":"Gestor Novo",
                  "status":"INACTIVE"
                }
                """;

        mockMvc.perform(put("/api/v1/departments/{id}", id)
                        .header("Authorization", "Bearer " + generateValidToken())
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value(DepartmentStatus.INACTIVE.name()));
    }

    @Test
    void updateShouldReturn404WhenDepartmentNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(departmentRepository.findById(id)).thenReturn(Optional.empty());

        String body = """
                {
                  "name":"Operações",
                  "managerName":"Gestor Novo",
                  "status":"INACTIVE"
                }
                """;

        mockMvc.perform(put("/api/v1/departments/{id}", id)
                        .header("Authorization", "Bearer " + generateValidToken())
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isNotFound());
    }
}