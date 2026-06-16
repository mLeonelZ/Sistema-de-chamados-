package com.helpdesk.integration;

import com.helpdesk.model.Category;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.enums.TicketPriority;
import com.helpdesk.model.enums.TicketStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DashboardControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void getStatsShouldReturn200ForAdmin() throws Exception {
        Category cat = new Category();
        cat.setId(UUID.randomUUID());
        cat.setName("Software");

        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID());
        ticket.setStatus(TicketStatus.ABERTO);
        ticket.setPriority(TicketPriority.BAIXA);
        ticket.setCategory(cat);

        when(ticketRepository.findAll()).thenReturn(List.of(ticket));

        mockMvc.perform(get("/api/v1/dashboard/stats")
                        .header("Authorization", "Bearer " + generateValidToken())) // Token ADMIN por padrão
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTickets").value(1))
                .andExpect(jsonPath("$.ticketsByCategory.Software").value(1));
    }
}
