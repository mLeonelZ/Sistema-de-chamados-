package com.helpdesk.integration;

import com.helpdesk.model.Category;
import com.helpdesk.model.SlaPolicy;
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
        when(ticketRepository.findAllWithDetails()).thenReturn(List.of(ticket));

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
        when(ticketRepository.findByIdWithDetails(id)).thenReturn(Optional.of(ticket));

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
        existing.setCode("CHM-0001");
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));

        Category category = new Category();
        category.setId(categoryId);

        User client = new User();
        client.setId(clientId);

        User assignee = new User();
        assignee.setId(assigneeId);

        when(ticketRepository.findByIdWithDetails(ticketId)).thenReturn(Optional.of(existing));
        when(categoryRepository.findByIdWithSlaPolicy(categoryId)).thenReturn(Optional.of(category));
        when(userRepository.findByIdWithDepartment(clientId)).thenReturn(Optional.of(client));
        when(userRepository.findByIdWithDepartment(assigneeId)).thenReturn(Optional.of(assignee));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String body = """
                {
                  "subject":"Novo assunto",
                  "description":"Nova descrição",
                  "categoryId":"%s",
                  "priority":"ALTA",
                  "channel":"PORTAL",
                  "clientId":"%s",
                  "assigneeId":"%s"
                }
                """.formatted(categoryId, clientId, assigneeId);

        mockMvc.perform(put("/api/v1/tickets/{id}", ticketId)
                        .header("Authorization", "Bearer " + generateValidToken())
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketId.toString()))
                .andExpect(jsonPath("$.code").value("CHM-0001")) // Code remains the same
                .andExpect(jsonPath("$.priority").value(TicketPriority.ALTA.name()))
                .andExpect(jsonPath("$.channel").value(TicketChannel.PORTAL.name()));
    }

    @Test
    void updateShouldReturn404WhenCategoryNotFound() throws Exception {
        UUID ticketId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        when(categoryRepository.findByIdWithSlaPolicy(categoryId)).thenReturn(Optional.empty());

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
    void createShouldReturn201() throws Exception {
        UUID categoryId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        
        Category category = new Category();
        category.setId(categoryId);
        
        User client = new User();
        client.setId(clientId);
        
        SlaPolicy sla = new SlaPolicy();
        sla.setResponseTimeMinutes(15);
        sla.setResolutionTimeMinutes(240);

        when(categoryRepository.findByIdWithSlaPolicy(categoryId)).thenReturn(Optional.of(category));
        when(userRepository.findByIdWithDepartment(clientId)).thenReturn(Optional.of(client));
        when(slaPolicyRepository.findByNameIgnoreCase("Crítico")).thenReturn(Optional.of(sla));
        when(ticketRepository.count()).thenReturn(0L);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));
        when(ticketRepository.findByIdWithDetails(any(UUID.class))).thenAnswer(i -> {
            Ticket t = new Ticket();
            t.setId((UUID) i.getArguments()[0]);
            t.setCode("CHM-0001");
            t.setStatus(TicketStatus.ABERTO);
            t.setCategory(category);
            t.setClient(client);
            t.setCreatedAt(LocalDateTime.now());
            return Optional.of(t);
        });

        String body = """
                {
                  "subject":"Problema na VPN",
                  "description":"Não consigo conectar",
                  "categoryId":"%s",
                  "priority":"CRITICA",
                  "channel":"PORTAL",
                  "clientId":"%s"
                }
                """.formatted(categoryId, clientId);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/tickets")
                        .header("Authorization", "Bearer " + generateValidToken())
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("CHM-0001"))
                .andExpect(jsonPath("$.status").value(TicketStatus.ABERTO.name()));
    }

    @Test
    void updateStatusShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        Ticket ticket = new Ticket();
        ticket.setId(id);
        ticket.setStatus(TicketStatus.ABERTO);
        
        when(ticketRepository.findByIdWithDetails(id)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/v1/tickets/{id}/status", id)
                        .param("status", "EM_ANDAMENTO")
                        .header("Authorization", "Bearer " + generateValidToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(TicketStatus.EM_ANDAMENTO.name()));
    }
}
