package com.helpdesk.dto.mapper;

import com.helpdesk.dto.category.CategoryRequestDto;
import com.helpdesk.dto.category.CategoryResponseDto;
import com.helpdesk.model.Category;
import com.helpdesk.model.SlaPolicy;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class CategoryMapper {

    public static Category toEntity(CategoryRequestDto dto, SlaPolicy slaPolicy) {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName(dto.name());
        category.setSlaPolicy(slaPolicy);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        return category;
    }

    public static CategoryResponseDto toResponse(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .slaPolicyId(category.getSlaPolicy() != null ? category.getSlaPolicy().getId() : null)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
