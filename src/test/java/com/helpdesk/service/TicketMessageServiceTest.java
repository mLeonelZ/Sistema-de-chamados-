package com.helpdesk.service;

import com.helpdesk.exception.ResourceNotFoundException;
import com.helpdesk.model.TicketMessage;
import com.helpdesk.repository.TicketMessageRepository;
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
class TicketMessageServiceTest {

    @Mock
    private TicketMessageRepository ticketMessageRepository;

    @InjectMocks
    private TicketMessageService ticketMessageService;

    @Test
    void findByIdShouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(ticketMessageRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> ticketMessageService.findById(id));
    }

    @Test
    void updateShouldKeepIdAndCreatedAt() {
        UUID id = UUID.randomUUID();
        TicketMessage existing = new TicketMessage();
        existing.setId(id);
        existing.setCreatedAt(LocalDateTime.now().minusMinutes(30));
        TicketMessage incoming = new TicketMessage();
        when(ticketMessageRepository.findById(id)).thenReturn(Optional.of(existing));
        when(ticketMessageRepository.save(incoming)).thenReturn(incoming);
        TicketMessage saved = ticketMessageService.update(id, incoming);
        assertEquals(id, saved.getId());
        assertEquals(existing.getCreatedAt(), saved.getCreatedAt());
    }
}
