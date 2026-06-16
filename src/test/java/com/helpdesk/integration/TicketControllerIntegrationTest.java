package com.helpdesk.integration;

import com.helpdesk.model.Category;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;
import com.helpdesk.model.enums.TicketChannel;
import com.helpdesk.model.enums.TicketPriority;
import com.helpdesk.model.enums.TicketStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TicketControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void findAllShouldReturn200() throws Exception {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        User client = new User();
        client.setId(UUID.randomUUID());
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID());
        ticket.setCode("CH-1");
        ticket.setSubject("Falha no sistema");
        ticket.setCategory(category);
        ticket.setClient(client);
        when(ticketRepository.findAll()).thenReturn(List.of(ticket));

        mockMvc.perform(get("/api/v1/tickets")
                        .header("Authorization", "Bearer " + generateValidToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("CH-1"));
    }

    @Test
    void findByIdShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        Category category = new Category();
        category.setId(UUID.randomUUID());
        User client = new User();
        client.setId(UUID.randomUUID());
        Ticket ticket = new Ticket();
        ticket.setId(id);
        ticket.setCode("CH-2");
        ticket.setSubject("Erro no login");
        ticket.setCategory(category);
        ticket.setClient(client);
        when(ticketRepository.findById(id)).thenReturn(Optional.of(ticket));

        mockMvc.perform(get("/api/v1/tickets/{id}", id)
                        .header("Authorization", "Bearer " + generateValidToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("Erro no login"));
    }

    @Test
    void updateShouldReturn200() throws Exception {
        UUID ticketId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();

        Ticket existing = new Ticket();
        existing.setId(ticketId);
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));

        Category category = new Category();
        category.setId(categoryId);

        User client = new User();
        client.setId(clientId);

        User assignee = new User();
        assignee.setId(assigneeId);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(userRepository.findById(assigneeId)).thenReturn(Optional.of(assignee));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String body = """
                {
                  "code":"CH-10",
                  "subject":"Novo assunto",
                  "description":"Nova descrição",
                  "categoryId":"%s",
                  "priority":"ALTA",
                  "status":"EM_ANDAMENTO",
                  "channel":"PORTAL",
                  "clientId":"%s",
                  "assigneeId":"%s",
                  "slaFirstResponseDeadline":"2026-01-01T10:00:00",
                  "slaResolutionDeadline":"2026-01-02T10:00:00",
                  "firstResponseAt":"2026-01-01T11:00:00",
                  "resolvedAt":null,
                  "closedAt":null
                }
                """.formatted(categoryId, clientId, assigneeId);

        mockMvc.perform(put("/api/v1/tickets/{id}", ticketId)
                        .header("Authorization", "Bearer " + generateValidToken())
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketId.toString()))
                .andExpect(jsonPath("$.code").value("CH-10"))
                .andExpect(jsonPath("$.priority").value(TicketPriority.ALTA.name()))
                .andExpect(jsonPath("$.status").value(TicketStatus.EM_ANDAMENTO.name()))
                .andExpect(jsonPath("$.channel").value(TicketChannel.PORTAL.name()));
    }

    @Test
    void updateShouldReturn404WhenCategoryNotFound() throws Exception {
        UUID ticketId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        String body = """
                {
                  "code":"CH-20",
                  "subject":"Assunto",
                  "description":"Descrição",
                  "categoryId":"%s",
                  "priority":"MEDIA",
                  "status":"ABERTO",
                  "channel":"EMAIL",
                  "clientId":"%s",
                  "assigneeId":null,
                  "slaFirstResponseDeadline":null,
                  "slaResolutionDeadline":null,
                  "firstResponseAt":null,
                  "resolvedAt":null,
                  "closedAt":null
                }
                """.formatted(categoryId, clientId);

        mockMvc.perform(put("/api/v1/tickets/{id}", ticketId)
                        .header("Authorization", "Bearer " + generateValidToken())
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateShouldReturn404WhenTicketNotFound() throws Exception {
        UUID ticketId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        Category category = new Category();
        category.setId(categoryId);
        User client = new User();
        client.setId(clientId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        String body = """
                {
                  "code":"CH-21",
                  "subject":"Assunto",
                  "description":"Descrição",
                  "categoryId":"%s",
                  "priority":"MEDIA",
                  "status":"ABERTO",
                  "channel":"EMAIL",
                  "clientId":"%s",
                  "assigneeId":null,
                  "slaFirstResponseDeadline":null,
                  "slaResolutionDeadline":null,
                  "firstResponseAt":null,
                  "resolvedAt":null,
                  "closedAt":null
                }
                """.formatted(categoryId, clientId);

        mockMvc.perform(put("/api/v1/tickets/{id}", ticketId)
                        .header("Authorization", "Bearer " + generateValidToken())
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isNotFound());
    }
}
