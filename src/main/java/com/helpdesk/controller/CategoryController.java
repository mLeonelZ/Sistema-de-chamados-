package com.helpdesk.controller;

import com.helpdesk.dto.mapper.CategoryMapper;
import com.helpdesk.dto.category.CategoryRequestDto;
import com.helpdesk.dto.category.CategoryResponseDto;
import com.helpdesk.model.Category;
import com.helpdesk.service.CategoryService;
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
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final SlaPolicyService slaPolicyService;

    public CategoryController(CategoryService categoryService, SlaPolicyService slaPolicyService) {
        this.categoryService = categoryService;
        this.slaPolicyService = slaPolicyService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> findAll() {
        return ResponseEntity.ok(categoryService.findAll().stream().map(CategoryMapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(CategoryMapper.toResponse(categoryService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> create(@RequestBody CategoryRequestDto dto) {
        Category category = CategoryMapper.toEntity(dto, slaPolicyService.findById(dto.slaPolicyId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoryMapper.toResponse(categoryService.save(category)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> update(@PathVariable UUID id, @RequestBody CategoryRequestDto dto) {
        Category category = CategoryMapper.toEntity(dto, slaPolicyService.findById(dto.slaPolicyId()));
        return ResponseEntity.ok(CategoryMapper.toResponse(categoryService.update(id, category)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
