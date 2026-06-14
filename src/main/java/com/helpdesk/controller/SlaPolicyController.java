package com.helpdesk.controller;

import com.helpdesk.dto.FieldMapper;
import com.helpdesk.dto.sla.SlaPolicyRequestDto;
import com.helpdesk.dto.sla.SlaPolicyResponseDto;
import com.helpdesk.model.SlaPolicy;
import com.helpdesk.service.SlaPolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sla-policies")
public class SlaPolicyController {

    private final SlaPolicyService slaPolicyService;

    public SlaPolicyController(SlaPolicyService slaPolicyService) {
        this.slaPolicyService = slaPolicyService;
    }

    @GetMapping
    public ResponseEntity<List<SlaPolicyResponseDto>> findAll() {
        return ResponseEntity.ok(slaPolicyService.findAll().stream().map(this::toResponseDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SlaPolicyResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponseDto(slaPolicyService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<SlaPolicyResponseDto> create(@RequestBody SlaPolicyRequestDto dto) {
        SlaPolicy slaPolicy = new SlaPolicy();
        FieldMapper.write(slaPolicy, "id", UUID.randomUUID());
        FieldMapper.write(slaPolicy, "name", dto.name());
        FieldMapper.write(slaPolicy, "responseTimeMinutes", dto.responseTimeMinutes());
        FieldMapper.write(slaPolicy, "resolutionTimeMinutes", dto.resolutionTimeMinutes());
        FieldMapper.write(slaPolicy, "createdAt", LocalDateTime.now());
        FieldMapper.write(slaPolicy, "updatedAt", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(slaPolicyService.save(slaPolicy)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SlaPolicyResponseDto> update(@PathVariable UUID id, @RequestBody SlaPolicyRequestDto dto) {
        SlaPolicy slaPolicy = new SlaPolicy();
        FieldMapper.write(slaPolicy, "name", dto.name());
        FieldMapper.write(slaPolicy, "responseTimeMinutes", dto.responseTimeMinutes());
        FieldMapper.write(slaPolicy, "resolutionTimeMinutes", dto.resolutionTimeMinutes());
        FieldMapper.write(slaPolicy, "updatedAt", LocalDateTime.now());
        return ResponseEntity.ok(toResponseDto(slaPolicyService.update(id, slaPolicy)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        slaPolicyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private SlaPolicyResponseDto toResponseDto(SlaPolicy slaPolicy) {
        return new SlaPolicyResponseDto(
                FieldMapper.read(slaPolicy, "id", UUID.class),
                FieldMapper.read(slaPolicy, "name", String.class),
                FieldMapper.read(slaPolicy, "responseTimeMinutes", Integer.class),
                FieldMapper.read(slaPolicy, "resolutionTimeMinutes", Integer.class),
                FieldMapper.read(slaPolicy, "createdAt", LocalDateTime.class),
                FieldMapper.read(slaPolicy, "updatedAt", LocalDateTime.class)
        );
    }
}
