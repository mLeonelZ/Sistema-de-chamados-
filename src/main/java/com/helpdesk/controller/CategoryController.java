package com.helpdesk.controller;

import com.helpdesk.dto.FieldMapper;
import com.helpdesk.dto.category.CategoryRequestDto;
import com.helpdesk.dto.category.CategoryResponseDto;
import com.helpdesk.model.Category;
import com.helpdesk.model.SlaPolicy;
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

import java.time.LocalDateTime;
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
        return ResponseEntity.ok(categoryService.findAll().stream().map(this::toResponseDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponseDto(categoryService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> create(@RequestBody CategoryRequestDto dto) {
        Category category = new Category();
        FieldMapper.write(category, "id", UUID.randomUUID());
        FieldMapper.write(category, "name", dto.name());
        FieldMapper.write(category, "slaPolicy", slaPolicyService.findById(dto.slaPolicyId()));
        FieldMapper.write(category, "createdAt", LocalDateTime.now());
        FieldMapper.write(category, "updatedAt", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(categoryService.save(category)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> update(@PathVariable UUID id, @RequestBody CategoryRequestDto dto) {
        Category category = new Category();
        FieldMapper.write(category, "name", dto.name());
        FieldMapper.write(category, "slaPolicy", slaPolicyService.findById(dto.slaPolicyId()));
        FieldMapper.write(category, "updatedAt", LocalDateTime.now());
        return ResponseEntity.ok(toResponseDto(categoryService.update(id, category)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private CategoryResponseDto toResponseDto(Category category) {
        SlaPolicy slaPolicy = FieldMapper.read(category, "slaPolicy", SlaPolicy.class);
        return new CategoryResponseDto(
                FieldMapper.read(category, "id", UUID.class),
                FieldMapper.read(category, "name", String.class),
                slaPolicy == null ? null : FieldMapper.read(slaPolicy, "id", UUID.class),
                FieldMapper.read(category, "createdAt", LocalDateTime.class),
                FieldMapper.read(category, "updatedAt", LocalDateTime.class)
        );
    }
}
