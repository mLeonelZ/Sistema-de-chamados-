package com.helpdesk.integration;

import com.helpdesk.model.Notification;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;
import com.helpdesk.model.enums.NotificationType;
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

class NotificationControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void findAllShouldReturn200() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setUser(user);
        notification.setTitle("Alerta");
        notification.setMessage("SLA próximo do vencimento");
        notification.setType(NotificationType.ALERTA_SLA);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        when(notificationRepository.findAll()).thenReturn(List.of(notification));

        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Alerta"));
    }

    @Test
    void findByIdShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        Notification notification = new Notification();
        notification.setId(id);
        notification.setUser(user);
        notification.setTitle("Info");
        notification.setMessage("Notificação");
        notification.setType(NotificationType.INFO);
        notification.setRead(true);
        notification.setCreatedAt(LocalDateTime.now());
        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));

        mockMvc.perform(get("/api/v1/notifications/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value(NotificationType.INFO.name()));
    }

    @Test
    void updateShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        Notification existing = new Notification();
        existing.setId(id);
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(notificationRepository.findById(id)).thenReturn(Optional.of(existing));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String body = """
                {
                  "userId":"%s",
                  "ticketId":"%s",
                  "title":"Novo título",
                  "message":"Nova mensagem",
                  "type":"SUCCESS",
                  "read":true
                }
                """.formatted(userId, ticketId);

        mockMvc.perform(put("/api/v1/notifications/{id}", id).contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.type").value(NotificationType.SUCCESS.name()))
                .andExpect(jsonPath("$.read").value(true));
    }

    @Test
    void updateShouldReturn404WhenUserNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        String body = """
                {
                  "userId":"%s",
                  "ticketId":null,
                  "title":"Novo título",
                  "message":"Nova mensagem",
                  "type":"SUCCESS",
                  "read":true
                }
                """.formatted(userId);

        mockMvc.perform(put("/api/v1/notifications/{id}", id).contentType("application/json").content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateShouldReturn404WhenNotificationNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        String body = """
                {
                  "userId":"%s",
                  "ticketId":null,
                  "title":"Novo título",
                  "message":"Nova mensagem",
                  "type":"SUCCESS",
                  "read":true
                }
                """.formatted(userId);

        mockMvc.perform(put("/api/v1/notifications/{id}", id).contentType("application/json").content(body))
                .andExpect(status().isNotFound());
    }
}
