package com.helpdesk.integration;

import com.helpdesk.model.Ticket;
import com.helpdesk.model.TicketMessage;
import com.helpdesk.model.User;
import com.helpdesk.model.enums.MessageType;
import com.helpdesk.model.enums.Role;
import com.helpdesk.model.enums.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TicketMessageControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void findByTicketIdShouldReturnOnlyPublicForClient() throws Exception {
        UUID ticketId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        
        User client = new User();
        client.setId(clientId);
        client.setRole(Role.CLIENTE);
        
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        
        TicketMessage m1 = new TicketMessage();
        m1.setType(MessageType.PUBLIC);
        m1.setText("Público");
        m1.setTicket(ticket);
        
        TicketMessage m2 = new TicketMessage();
        m2.setType(MessageType.INTERNAL);
        m2.setText("Interno");
        m2.setTicket(ticket);

        // Simula o usuário logado no token
        // generateValidToken usa ADMIN por padrão no AbstractIntegrationTest, 
        // mas o controlador busca o usuário pelo ID vindo do token (authentication.getName())
        // Precisamos mockar o findById do UserService para retornar o perfil correto
        when(userRepository.findByIdWithDepartment(any(UUID.class))).thenReturn(Optional.of(client));
        when(ticketMessageRepository.findByTicketIdOrderByCreatedAtAsc(ticketId)).thenReturn(List.of(m1, m2));

        mockMvc.perform(get("/api/v1/tickets/{ticketId}/messages", ticketId)
                        .header("Authorization", "Bearer " + generateValidToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].text").value("Público"));
    }

    @Test
    void createShouldReturn201() throws Exception {
        UUID ticketId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        User author = new User();
        author.setId(authorId);
        author.setRole(Role.ADMIN); // Defina a role para evitar null pointers ou falso-positivos
        
        when(ticketRepository.findByIdWithDetails(ticketId)).thenReturn(Optional.of(ticket));
        when(userRepository.findByIdWithDepartment(any(UUID.class))).thenReturn(Optional.of(author));
        when(ticketMessageRepository.save(any(TicketMessage.class))).thenAnswer(i -> i.getArgument(0));

        String body = """
                {
                  "ticketId":"%s",
                  "authorId":"%s",
                  "type":"PUBLIC",
                  "text":"Nova mensagem"
                }
                """.formatted(ticketId, authorId);

        mockMvc.perform(post("/api/v1/tickets/{ticketId}/messages", ticketId)
                        .header("Authorization", "Bearer " + generateValidToken())
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value("Nova mensagem"));
    }

    @Test
    void createInternalMessageAsClientShouldReturn401() throws Exception {
        UUID ticketId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        User client = new User();
        client.setId(authorId);
        client.setRole(Role.CLIENTE);
        
        when(ticketRepository.findByIdWithDetails(ticketId)).thenReturn(Optional.of(ticket));
        when(userRepository.findByIdWithDepartment(any(UUID.class))).thenReturn(Optional.of(client));

        String body = """
                {
                  "ticketId":"%s",
                  "authorId":"%s",
                  "type":"INTERNAL",
                  "text":"Mensagem privada tentada por cliente"
                }
                """.formatted(ticketId, authorId);

        mockMvc.perform(post("/api/v1/tickets/{ticketId}/messages", ticketId)
                        .header("Authorization", "Bearer " + generateValidToken())
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}
