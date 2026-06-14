package com.helpdesk.integration;

import com.helpdesk.model.Ticket;
import com.helpdesk.model.TicketMessage;
import com.helpdesk.model.User;
import com.helpdesk.model.enums.MessageType;
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

class TicketMessageControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void findAllShouldReturn200() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID());
        User author = new User();
        author.setId(UUID.randomUUID());
        TicketMessage message = new TicketMessage();
        message.setId(UUID.randomUUID());
        message.setTicket(ticket);
        message.setAuthor(author);
        message.setType(MessageType.PUBLIC);
        message.setText("Mensagem inicial");
        message.setCreatedAt(LocalDateTime.now());
        when(ticketMessageRepository.findAll()).thenReturn(List.of(message));

        mockMvc.perform(get("/api/v1/ticket-messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Mensagem inicial"));
    }

    @Test
    void findByIdShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID());
        User author = new User();
        author.setId(UUID.randomUUID());
        TicketMessage message = new TicketMessage();
        message.setId(id);
        message.setTicket(ticket);
        message.setAuthor(author);
        message.setType(MessageType.INTERNAL);
        message.setText("Mensagem interna");
        message.setCreatedAt(LocalDateTime.now());
        when(ticketMessageRepository.findById(id)).thenReturn(Optional.of(message));

        mockMvc.perform(get("/api/v1/ticket-messages/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value(MessageType.INTERNAL.name()));
    }

    @Test
    void updateShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        User author = new User();
        author.setId(authorId);
        TicketMessage existing = new TicketMessage();
        existing.setId(id);
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(ticketMessageRepository.findById(id)).thenReturn(Optional.of(existing));
        when(ticketMessageRepository.save(any(TicketMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String body = """
                {
                  "ticketId":"%s",
                  "authorId":"%s",
                  "type":"PUBLIC",
                  "text":"Texto atualizado"
                }
                """.formatted(ticketId, authorId);

        mockMvc.perform(put("/api/v1/ticket-messages/{id}", id).contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.text").value("Texto atualizado"));
    }

    @Test
    void updateShouldReturn404WhenTicketNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        String body = """
                {
                  "ticketId":"%s",
                  "authorId":"%s",
                  "type":"PUBLIC",
                  "text":"Texto"
                }
                """.formatted(ticketId, authorId);

        mockMvc.perform(put("/api/v1/ticket-messages/{id}", id).contentType("application/json").content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateShouldReturn404WhenMessageNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        User author = new User();
        author.setId(authorId);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(ticketMessageRepository.findById(id)).thenReturn(Optional.empty());

        String body = """
                {
                  "ticketId":"%s",
                  "authorId":"%s",
                  "type":"PUBLIC",
                  "text":"Texto"
                }
                """.formatted(ticketId, authorId);

        mockMvc.perform(put("/api/v1/ticket-messages/{id}", id).contentType("application/json").content(body))
                .andExpect(status().isNotFound());
    }
}
