package com.helpdesk.service;

import com.helpdesk.exception.ResourceNotFoundException;
import com.helpdesk.model.Ticket;
import com.helpdesk.repository.TicketRepository;
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
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void findByCodeShouldThrowWhenNotFound() {
        when(ticketRepository.findByCode("X")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> ticketService.findByCode("X"));
    }

    @Test
    void updateShouldKeepIdAndCreatedAt() {
        UUID id = UUID.randomUUID();
        Ticket existing = new Ticket();
        existing.setId(id);
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));
        Ticket incoming = new Ticket();
        when(ticketRepository.findById(id)).thenReturn(Optional.of(existing));
        when(ticketRepository.save(incoming)).thenReturn(incoming);
        Ticket saved = ticketService.update(id, incoming);
        assertEquals(id, saved.getId());
        assertEquals(existing.getCreatedAt(), saved.getCreatedAt());
    }
}
