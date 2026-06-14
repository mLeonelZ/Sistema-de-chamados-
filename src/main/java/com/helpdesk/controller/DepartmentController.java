package com.helpdesk.controller;

import com.helpdesk.dto.FieldMapper;
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

import java.time.LocalDateTime;
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
        return ResponseEntity.ok(departmentService.findAll().stream().map(this::toResponseDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponseDto(departmentService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<DepartmentResponseDto> create(@RequestBody DepartmentRequestDto dto) {
        Department department = new Department();
        FieldMapper.write(department, "id", UUID.randomUUID());
        FieldMapper.write(department, "name", dto.name());
        FieldMapper.write(department, "managerName", dto.managerName());
        FieldMapper.write(department, "status", dto.status());
        FieldMapper.write(department, "createdAt", LocalDateTime.now());
        FieldMapper.write(department, "updatedAt", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(departmentService.save(department)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponseDto> update(@PathVariable UUID id, @RequestBody DepartmentRequestDto dto) {
        Department department = new Department();
        FieldMapper.write(department, "name", dto.name());
        FieldMapper.write(department, "managerName", dto.managerName());
        FieldMapper.write(department, "status", dto.status());
        FieldMapper.write(department, "updatedAt", LocalDateTime.now());
        return ResponseEntity.ok(toResponseDto(departmentService.update(id, department)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        departmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private DepartmentResponseDto toResponseDto(Department department) {
        return new DepartmentResponseDto(
                FieldMapper.read(department, "id", UUID.class),
                FieldMapper.read(department, "name", String.class),
                FieldMapper.read(department, "managerName", String.class),
                FieldMapper.read(department, "status", com.helpdesk.model.enums.DepartmentStatus.class),
                FieldMapper.read(department, "createdAt", LocalDateTime.class),
                FieldMapper.read(department, "updatedAt", LocalDateTime.class)
        );
    }
}
