package com.helpdesk.integration;

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

class SlaPolicyControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void findAllShouldReturn200() throws Exception {
        SlaPolicy policy = new SlaPolicy();
        policy.setId(UUID.randomUUID());
        policy.setName("Critico");
        policy.setResponseTimeMinutes(15);
        policy.setResolutionTimeMinutes(60);
        policy.setCreatedAt(LocalDateTime.now().minusDays(1));
        policy.setUpdatedAt(LocalDateTime.now());
        when(slaPolicyRepository.findAll()).thenReturn(List.of(policy));

        mockMvc.perform(get("/api/v1/sla-policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Critico"));
    }

    @Test
    void findByIdShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        SlaPolicy policy = new SlaPolicy();
        policy.setId(id);
        policy.setName("Alto");
        policy.setResponseTimeMinutes(30);
        policy.setResolutionTimeMinutes(120);
        policy.setCreatedAt(LocalDateTime.now().minusDays(1));
        policy.setUpdatedAt(LocalDateTime.now());
        when(slaPolicyRepository.findById(id)).thenReturn(Optional.of(policy));

        mockMvc.perform(get("/api/v1/sla-policies/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alto"));
    }

    @Test
    void updateShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();

        SlaPolicy existing = new SlaPolicy();
        existing.setId(id);
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));

        when(slaPolicyRepository.findById(id)).thenReturn(Optional.of(existing));
        when(slaPolicyRepository.save(any(SlaPolicy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String body = """
                {
                  "name":"Muito Alto",
                  "responseTimeMinutes":10,
                  "resolutionTimeMinutes":45
                }
                """;

        mockMvc.perform(put("/api/v1/sla-policies/{id}", id).contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Muito Alto"));
    }

    @Test
    void updateShouldReturn404WhenPolicyNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(slaPolicyRepository.findById(id)).thenReturn(Optional.empty());

        String body = """
                {
                  "name":"Muito Alto",
                  "responseTimeMinutes":10,
                  "resolutionTimeMinutes":45
                }
                """;

        mockMvc.perform(put("/api/v1/sla-policies/{id}", id).contentType("application/json").content(body))
                .andExpect(status().isNotFound());
    }
}