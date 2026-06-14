package com.helpdesk.controller;

import com.helpdesk.dto.mapper.UserMapper;
import com.helpdesk.dto.user.UserRequestDto;
import com.helpdesk.dto.user.UserResponseDto;
import com.helpdesk.model.Department;
import com.helpdesk.service.DepartmentService;
import com.helpdesk.service.UserService;
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
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final DepartmentService departmentService;

    public UserController(UserService userService, DepartmentService departmentService) {
        this.userService = userService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findAll() {
        return ResponseEntity.ok(userService.findAll().stream().map(UserMapper::toResponseDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(UserMapper.toResponseDto(userService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody UserRequestDto dto) {
        Department department = dto.departmentId() == null ? null : departmentService.findById(dto.departmentId());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toResponseDto(userService.save(UserMapper.toEntity(dto, department))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
