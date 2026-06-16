package com.helpdesk.service;

import com.helpdesk.exception.ResourceNotFoundException;
import com.helpdesk.model.TicketMessage;
import com.helpdesk.model.enums.MessageType;
import com.helpdesk.repository.TicketMessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketMessageServiceTest {

    @Mock
    private TicketMessageRepository ticketMessageRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TicketMessageService ticketMessageService;

    @Test
    void findByIdShouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(ticketMessageRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> ticketMessageService.findById(id));
    }

    @Test
    void saveShouldSetIdAndCreatedAt() {
        TicketMessage message = new TicketMessage();
        message.setText("Test");
        
        when(ticketMessageRepository.save(any(TicketMessage.class))).thenAnswer(i -> i.getArgument(0));
        
        TicketMessage saved = ticketMessageService.save(message);
        
        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertEquals("Test", saved.getText());
    }

    @Test
    void findByTicketIdShouldFilterInternalMessages() {
        UUID ticketId = UUID.randomUUID();
        TicketMessage m1 = new TicketMessage();
        m1.setType(MessageType.PUBLIC);
        TicketMessage m2 = new TicketMessage();
        m2.setType(MessageType.INTERNAL);
        
        when(ticketMessageRepository.findByTicketIdOrderByCreatedAtAsc(ticketId)).thenReturn(List.of(m1, m2));
        
        List<TicketMessage> publicOnly = ticketMessageService.findByTicketId(ticketId, false);
        List<TicketMessage> all = ticketMessageService.findByTicketId(ticketId, true);
        
        assertEquals(1, publicOnly.size());
        assertEquals(MessageType.PUBLIC, publicOnly.get(0).getType());
        assertEquals(2, all.size());
    }
}
