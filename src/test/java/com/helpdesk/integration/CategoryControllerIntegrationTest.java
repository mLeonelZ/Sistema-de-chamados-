package com.helpdesk.integration;

import com.helpdesk.model.Category;
import com.helpdesk.model.SlaPolicy;
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

class CategoryControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void findAllShouldReturn200() throws Exception {
        SlaPolicy sla = new SlaPolicy();
        sla.setId(UUID.randomUUID());
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Hardware");
        category.setSlaPolicy(sla);
        category.setCreatedAt(LocalDateTime.now().minusDays(1));
        category.setUpdatedAt(LocalDateTime.now());
        when(categoryRepository.findAllWithSlaPolicy()).thenReturn(List.of(category));

        mockMvc.perform(get("/api/v1/categories")
                .header("Authorization", "Bearer " + generateValidToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Hardware"));
    }

    @Test
    void findByIdShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        SlaPolicy sla = new SlaPolicy();
        sla.setId(UUID.randomUUID());
        Category category = new Category();
        category.setId(id);
        category.setName("Software");
        category.setSlaPolicy(sla);
        category.setCreatedAt(LocalDateTime.now().minusDays(1));
        category.setUpdatedAt(LocalDateTime.now());
        when(categoryRepository.findByIdWithSlaPolicy(id)).thenReturn(Optional.of(category));

        mockMvc.perform(get("/api/v1/categories/{id}", id)
                .header("Authorization", "Bearer " + generateValidToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Software"));
    }

    @Test
    void updateShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        UUID slaId = UUID.randomUUID();

        SlaPolicy sla = new SlaPolicy();
        sla.setId(slaId);

        Category existing = new Category();
        existing.setId(id);
        existing.setCreatedAt(LocalDateTime.now().minusDays(2));

        when(slaPolicyRepository.findById(slaId)).thenReturn(Optional.of(sla));
        when(categoryRepository.findByIdWithSlaPolicy(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String body = """
                {
                  "name":"Banco de Dados",
                  "slaPolicyId":"%s"
                }
                """.formatted(slaId);

        mockMvc.perform(put("/api/v1/categories/{id}", id)
                .contentType("application/json")
                .content(body)
                .header("Authorization", "Bearer " + generateValidToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Banco de Dados"));
    }

    @Test
    void updateShouldReturn404WhenSlaNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        UUID slaId = UUID.randomUUID();

        when(slaPolicyRepository.findById(slaId)).thenReturn(Optional.empty());

        String body = """
                {
                  "name":"Banco de Dados",
                  "slaPolicyId":"%s"
                }
                """.formatted(slaId);

        mockMvc.perform(put("/api/v1/categories/{id}", id)
                .contentType("application/json")
                .content(body)
                .header("Authorization", "Bearer " + generateValidToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateShouldReturn404WhenCategoryNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        UUID slaId = UUID.randomUUID();

        SlaPolicy sla = new SlaPolicy();
        sla.setId(slaId);

        when(slaPolicyRepository.findById(slaId)).thenReturn(Optional.of(sla));
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        String body = """
                {
                  "name":"Banco de Dados",
                  "slaPolicyId":"%s"
                }
                """.formatted(slaId);

        mockMvc.perform(put("/api/v1/categories/{id}", id)
                .contentType("application/json")
                .content(body)
                .header("Authorization", "Bearer " + generateValidToken()))
                .andExpect(status().isNotFound());
    }
}