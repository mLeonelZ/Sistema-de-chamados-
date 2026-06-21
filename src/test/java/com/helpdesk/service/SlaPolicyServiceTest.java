package com.helpdesk.service;

import com.helpdesk.exception.ResourceNotFoundException;
import com.helpdesk.model.SlaPolicy;
import com.helpdesk.repository.SlaPolicyRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SlaPolicyServiceTest {

    @Mock
    private SlaPolicyRepository slaPolicyRepository;

    @InjectMocks
    private SlaPolicyService slaPolicyService;

    @Test
    void findByIdShouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(slaPolicyRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> slaPolicyService.findById(id));
    }

    @Test
    void updateShouldKeepIdAndCreatedAt() {
        UUID id = UUID.randomUUID();
        SlaPolicy existing = new SlaPolicy();
        existing.setId(id);
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        SlaPolicy incoming = new SlaPolicy();
        when(slaPolicyRepository.findById(id)).thenReturn(Optional.of(existing));
        when(slaPolicyRepository.save(incoming)).thenReturn(incoming);
        SlaPolicy saved = slaPolicyService.update(id, incoming);
        assertEquals(id, saved.getId());
        assertEquals(existing.getCreatedAt(), saved.getCreatedAt());
    }
}
