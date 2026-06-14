package com.helpdesk.service;

import com.helpdesk.exception.ResourceNotFoundException;
import com.helpdesk.model.Notification;
import com.helpdesk.repository.NotificationRepository;
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
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void findByIdShouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(notificationRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> notificationService.findById(id));
    }

    @Test
    void updateShouldKeepIdAndCreatedAt() {
        UUID id = UUID.randomUUID();
        Notification existing = new Notification();
        existing.setId(id);
        existing.setCreatedAt(LocalDateTime.now().minusHours(4));
        Notification incoming = new Notification();
        when(notificationRepository.findById(id)).thenReturn(Optional.of(existing));
        when(notificationRepository.save(incoming)).thenReturn(incoming);
        Notification saved = notificationService.update(id, incoming);
        assertEquals(id, saved.getId());
        assertEquals(existing.getCreatedAt(), saved.getCreatedAt());
    }
}
