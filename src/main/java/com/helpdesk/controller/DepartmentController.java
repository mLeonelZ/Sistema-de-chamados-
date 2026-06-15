package com.helpdesk.controller;

import com.helpdesk.dto.mapper.DepartmentMapper;
import com.helpdesk.dto.department.DepartmentRequestDto;
import com.helpdesk.dto.department.DepartmentResponseDto;
import com.helpdesk.model.Department;
import com.helpdesk.service.DepartmentService;
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
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponseDto>> findAll() {
        return ResponseEntity.ok(departmentService.findAll().stream().map(DepartmentMapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(DepartmentMapper.toResponse(departmentService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<DepartmentResponseDto> create(@RequestBody DepartmentRequestDto dto) {
        Department department = DepartmentMapper.toEntity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(DepartmentMapper.toResponse(departmentService.save(department)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponseDto> update(@PathVariable UUID id, @RequestBody DepartmentRequestDto dto) {
        Department department = DepartmentMapper.toEntity(dto);
        return ResponseEntity.ok(DepartmentMapper.toResponse(departmentService.update(id, department)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        departmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
