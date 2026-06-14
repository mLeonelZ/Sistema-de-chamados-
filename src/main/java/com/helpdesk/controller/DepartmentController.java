package com.helpdesk.controller;

import com.helpdesk.model.Department;
import com.helpdesk.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<List<Department>> findAll() {
        return ResponseEntity.ok(departmentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(departmentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Department> create(@RequestBody Department department) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.save(department));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        departmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
