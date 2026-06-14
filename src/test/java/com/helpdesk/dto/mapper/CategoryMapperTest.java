package com.helpdesk.dto.mapper;

import com.helpdesk.dto.category.CategoryRequestDto;
import com.helpdesk.dto.category.CategoryResponseDto;
import com.helpdesk.model.Category;
import com.helpdesk.model.SlaPolicy;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CategoryMapperTest {

    @Test
    void toEntityShouldMapFields() {
        SlaPolicy slaPolicy = new SlaPolicy();
        slaPolicy.setId(UUID.randomUUID());
        CategoryRequestDto dto = new CategoryRequestDto("Hardware", slaPolicy.getId());
        Category category = CategoryMapper.toEntity(dto, slaPolicy);
        assertNotNull(category.getId());
        assertEquals("Hardware", category.getName());
        assertEquals(slaPolicy, category.getSlaPolicy());
        assertNotNull(category.getCreatedAt());
        assertNotNull(category.getUpdatedAt());
    }

    @Test
    void toResponseDtoShouldMapFields() {
        SlaPolicy slaPolicy = new SlaPolicy();
        UUID slaId = UUID.randomUUID();
        slaPolicy.setId(slaId);
        Category category = new Category();
        UUID id = UUID.randomUUID();
        category.setId(id);
        category.setName("Software");
        category.setSlaPolicy(slaPolicy);
        category.setCreatedAt(LocalDateTime.now().minusDays(1));
        category.setUpdatedAt(LocalDateTime.now());
        CategoryResponseDto response = CategoryMapper.toResponseDto(category);
        assertEquals(id, response.id());
        assertEquals("Software", response.name());
        assertEquals(slaId, response.slaPolicyId());
    }
}
