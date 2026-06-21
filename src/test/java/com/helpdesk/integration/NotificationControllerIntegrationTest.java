package com.helpdesk.integration;

import com.helpdesk.model.Notification;
import com.helpdesk.model.User;
import com.helpdesk.model.enums.NotificationType;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void findMyNotificationsShouldReturn200() throws Exception {
        UUID userId = UUID.randomUUID();
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setTitle("Alerta");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        
        // Mocking the user retrieval from security context
        User currentUser = new User();
        currentUser.setId(userId);
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(currentUser));
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(any(UUID.class))).thenReturn(List.of(notification));

        mockMvc.perform(get("/api/v1/notifications")
                        .header("Authorization", "Bearer " + generateValidToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Alerta"));
    }

    @Test
    void markAsReadShouldReturn204() throws Exception {
        UUID id = UUID.randomUUID();
        Notification notification = new Notification();
        notification.setId(id);
        notification.setRead(false);
        
        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(patch("/api/v1/notifications/{id}/read", id)
                        .header("Authorization", "Bearer " + generateValidToken()))
                .andExpect(status().isNoContent());
    }

    @Test
    void countUnreadShouldReturnCount() throws Exception {
        when(notificationRepository.countByUserIdAndReadFalse(any(UUID.class))).thenReturn(5L);

        mockMvc.perform(get("/api/v1/notifications/unread/count")
                        .header("Authorization", "Bearer " + generateValidToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));
    }
}
