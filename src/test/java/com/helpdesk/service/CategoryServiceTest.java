package com.helpdesk.service;

import com.helpdesk.exception.ResourceNotFoundException;
import com.helpdesk.model.Category;
import com.helpdesk.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void findByIdShouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findByIdWithSlaPolicy(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> categoryService.findById(id));
    }

    @Test
    void saveShouldPersist() {
        Category category = new Category();
        when(categoryRepository.save(category)).thenReturn(category);
        assertEquals(category, categoryService.save(category));
    }

    @Test
    void updateShouldKeepIdAndCreatedAt() {
        UUID id = UUID.randomUUID();
        Category existing = new Category();
        existing.setId(id);
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        Category incoming = new Category();
        incoming.setUpdatedAt(LocalDateTime.now());
        when(categoryRepository.findByIdWithSlaPolicy(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(incoming)).thenReturn(incoming);
        Category saved = categoryService.update(id, incoming);
        assertEquals(id, saved.getId());
        assertEquals(existing.getCreatedAt(), saved.getCreatedAt());
        verify(categoryRepository).save(incoming);
    }
}
