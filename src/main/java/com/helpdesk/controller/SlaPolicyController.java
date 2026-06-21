package com.helpdesk.controller;

import com.helpdesk.dto.mapper.SlaPolicyMapper;
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
        return ResponseEntity.ok(slaPolicyService.findAll().stream().map(SlaPolicyMapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SlaPolicyResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(SlaPolicyMapper.toResponse(slaPolicyService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<SlaPolicyResponseDto> create(@RequestBody SlaPolicyRequestDto dto) {
        SlaPolicy slaPolicy = SlaPolicyMapper.toEntity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(SlaPolicyMapper.toResponse(slaPolicyService.save(slaPolicy)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SlaPolicyResponseDto> update(@PathVariable UUID id, @RequestBody SlaPolicyRequestDto dto) {
        SlaPolicy slaPolicy = SlaPolicyMapper.toEntity(dto);
        return ResponseEntity.ok(SlaPolicyMapper.toResponse(slaPolicyService.update(id, slaPolicy)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        slaPolicyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
