package com.helpdesk.service;

import com.helpdesk.dto.dashboard.DashboardStatsResponseDto;
import com.helpdesk.model.Category;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.enums.TicketPriority;
import com.helpdesk.model.enums.TicketStatus;
import com.helpdesk.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getStatsShouldCalculateCorrectly() {
        Category cat = new Category();
        cat.setName("Hardware");

        Ticket t1 = new Ticket();
        t1.setStatus(TicketStatus.RESOLVIDO);
        t1.setPriority(TicketPriority.ALTA);
        t1.setCategory(cat);
        t1.setCreatedAt(LocalDateTime.now().minusHours(2));
        t1.setResolvedAt(LocalDateTime.now());

        Ticket t2 = new Ticket();
        t2.setStatus(TicketStatus.ABERTO);
        t2.setPriority(TicketPriority.MEDIA);
        t2.setCategory(cat);

        when(ticketRepository.findAll()).thenReturn(List.of(t1, t2));

        DashboardStatsResponseDto stats = dashboardService.getStats();

        assertEquals(2, stats.totalTickets());
        assertEquals(1, stats.resolvedTickets());
        assertEquals(1, stats.openTickets());
        assertEquals(2.0, stats.averageResolutionTimeHours());
        assertEquals(1L, stats.ticketsByPriority().get("ALTA"));
        assertEquals(2L, stats.ticketsByCategory().get("Hardware"));
    }
}
