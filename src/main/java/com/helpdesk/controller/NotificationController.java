package com.helpdesk.controller;

import com.helpdesk.dto.mapper.NotificationMapper;
import com.helpdesk.dto.notification.NotificationRequestDto;
import com.helpdesk.dto.notification.NotificationResponseDto;
import com.helpdesk.model.Notification;
import com.helpdesk.service.NotificationService;
import com.helpdesk.service.TicketService;
import com.helpdesk.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;
    private final TicketService ticketService;

    public NotificationController(NotificationService notificationService, UserService userService, TicketService ticketService) {
        this.notificationService = notificationService;
        this.userService = userService;
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> findMyNotifications(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(notificationService.findByUserId(userId).stream()
                .map(NotificationMapper::toResponse)
                .toList());
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> countUnread(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(notificationService.countUnreadByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(NotificationMapper.toResponse(notificationService.findById(id)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<NotificationResponseDto> create(@RequestBody @Valid NotificationRequestDto dto) {
        Notification notification = NotificationMapper.toEntity(
                dto,
                userService.findById(dto.userId()),
                dto.ticketId() == null ? null : ticketService.findById(dto.ticketId())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(NotificationMapper.toResponse(notificationService.save(notification)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        notificationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
