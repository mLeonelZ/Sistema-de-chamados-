package com.helpdesk.service;

import com.helpdesk.exception.ResourceNotFoundException;
import com.helpdesk.model.Notification;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;
import com.helpdesk.model.enums.NotificationType;
import com.helpdesk.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
    void markAsReadShouldSetReadToTrue() {
        UUID id = UUID.randomUUID();
        Notification n = new Notification();
        n.setId(id);
        n.setRead(false);
        
        when(notificationRepository.findById(id)).thenReturn(Optional.of(n));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));
        
        notificationService.markAsRead(id);
        
        assertTrue(n.getRead());
    }

    @Test
    void sendShouldCreateCorrectNotification() {
        User user = new User();
        user.setId(UUID.randomUUID());
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID());
        
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));
        
        Notification n = notificationService.send(user, ticket, "Title", "Message", NotificationType.NOVO_CHAMADO);
        
        assertNotNull(n.getId());
        assertEquals(user, n.getUser());
        assertEquals(ticket, n.getTicket());
        assertEquals("Title", n.getTitle());
        assertFalse(n.getRead());
    }
}
