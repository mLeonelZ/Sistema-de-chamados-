package com.helpdesk.dto.mapper;

import com.helpdesk.dto.sla.SlaPolicyRequestDto;
import com.helpdesk.dto.sla.SlaPolicyResponseDto;
import com.helpdesk.model.SlaPolicy;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SlaPolicyMapperTest {

    @Test
    void toEntityShouldMapFields() {
        SlaPolicyRequestDto dto = new SlaPolicyRequestDto("Crítico", 15, 60);
        SlaPolicy policy = SlaPolicyMapper.toEntity(dto);
        assertNotNull(policy.getId());
        assertEquals("Crítico", policy.getName());
        assertEquals(15, policy.getResponseTimeMinutes());
        assertEquals(60, policy.getResolutionTimeMinutes());
    }

    @Test
    void toResponseDtoShouldMapFields() {
        SlaPolicy policy = new SlaPolicy();
        UUID id = UUID.randomUUID();
        policy.setId(id);
        policy.setName("Alto");
        policy.setResponseTimeMinutes(30);
        policy.setResolutionTimeMinutes(120);
        policy.setCreatedAt(LocalDateTime.now().minusDays(1));
        policy.setUpdatedAt(LocalDateTime.now());
        SlaPolicyResponseDto response = SlaPolicyMapper.toResponse(policy);
        assertEquals(id, response.id());
        assertEquals("Alto", response.name());
        assertEquals(30, response.responseTimeMinutes());
    }
}
