package com.helpdesk.service;

import com.helpdesk.exception.ResourceNotFoundException;
import com.helpdesk.model.Category;
import com.helpdesk.model.SlaPolicy;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;
import com.helpdesk.model.enums.TicketStatus;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserService userService;

    @Mock
    private SlaPolicyService slaPolicyService;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void saveShouldGenerateCodeAndCalculateSla() {
        SlaPolicy sla = new SlaPolicy();
        sla.setResponseTimeMinutes(60);
        sla.setResolutionTimeMinutes(240);
        
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setSlaPolicy(sla);
        
        Ticket ticket = new Ticket();
        ticket.setCategory(category);
        ticket.setPriority(com.helpdesk.model.enums.TicketPriority.MEDIA);
        
        User client = new User();
        client.setId(UUID.randomUUID());
        ticket.setClient(client);
        
        when(categoryService.findById(category.getId())).thenReturn(category);
        when(userService.findById(client.getId())).thenReturn(client);
        when(slaPolicyService.findByName("Médio")).thenReturn(sla);

        when(ticketRepository.count()).thenReturn(5L);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));
        when(ticketRepository.findByIdWithDetails(any(UUID.class))).thenReturn(Optional.of(ticket));
        
        Ticket saved = ticketService.save(ticket);
        
        assertEquals("CHM-0006", saved.getCode());
        assertEquals(TicketStatus.ABERTO, saved.getStatus());
        assertNotNull(saved.getSlaFirstResponseDeadline());
        assertNotNull(saved.getSlaResolutionDeadline());
        assertEquals(saved.getCreatedAt().plusMinutes(60), saved.getSlaFirstResponseDeadline());
    }

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
        existing.setCode("CHM-0001");
        existing.setPriority(com.helpdesk.model.enums.TicketPriority.MEDIA);
        
        Ticket incoming = new Ticket();
        incoming.setSubject("New Subject");
        incoming.setPriority(com.helpdesk.model.enums.TicketPriority.MEDIA);
        
        when(ticketRepository.findByIdWithDetails(id)).thenReturn(Optional.of(existing));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));
        
        Ticket saved = ticketService.update(id, incoming);
        
        assertEquals(id, saved.getId());
        assertEquals(existing.getCreatedAt(), saved.getCreatedAt());
        assertEquals("CHM-0001", saved.getCode());
        assertEquals("New Subject", saved.getSubject());
    }
}
