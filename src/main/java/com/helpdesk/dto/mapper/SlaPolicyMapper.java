package com.helpdesk.dto.mapper;

import com.helpdesk.dto.sla.SlaPolicyRequestDto;
import com.helpdesk.dto.sla.SlaPolicyResponseDto;
import com.helpdesk.model.SlaPolicy;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class SlaPolicyMapper {

    public static SlaPolicy toEntity(SlaPolicyRequestDto dto) {
        SlaPolicy slaPolicy = new SlaPolicy();
        slaPolicy.setId(UUID.randomUUID());
        slaPolicy.setName(dto.name());
        slaPolicy.setResponseTimeMinutes(dto.responseTimeMinutes());
        slaPolicy.setResolutionTimeMinutes(dto.resolutionTimeMinutes());
        slaPolicy.setCreatedAt(LocalDateTime.now());
        slaPolicy.setUpdatedAt(LocalDateTime.now());
        return slaPolicy;
    }

    public static SlaPolicyResponseDto toResponse(SlaPolicy slaPolicy) {
        return SlaPolicyResponseDto.builder()
                .id(slaPolicy.getId())
                .name(slaPolicy.getName())
                .responseTimeMinutes(slaPolicy.getResponseTimeMinutes())
                .resolutionTimeMinutes(slaPolicy.getResolutionTimeMinutes())
                .createdAt(slaPolicy.getCreatedAt())
                .updatedAt(slaPolicy.getUpdatedAt())
                .build();
    }
}
