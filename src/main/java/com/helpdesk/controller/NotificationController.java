package com.helpdesk.controller;

import com.helpdesk.dto.FieldMapper;
import com.helpdesk.dto.notification.NotificationRequestDto;
import com.helpdesk.dto.notification.NotificationResponseDto;
import com.helpdesk.model.Notification;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;
import com.helpdesk.service.NotificationService;
import com.helpdesk.service.TicketService;
import com.helpdesk.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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
    public ResponseEntity<List<NotificationResponseDto>> findAll() {
        return ResponseEntity.ok(notificationService.findAll().stream().map(this::toResponseDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponseDto(notificationService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<NotificationResponseDto> create(@RequestBody NotificationRequestDto dto) {
        Notification notification = new Notification();
        FieldMapper.write(notification, "id", UUID.randomUUID());
        FieldMapper.write(notification, "user", userService.findById(dto.userId()));
        FieldMapper.write(notification, "ticket", dto.ticketId() == null ? null : ticketService.findById(dto.ticketId()));
        FieldMapper.write(notification, "title", dto.title());
        FieldMapper.write(notification, "message", dto.message());
        FieldMapper.write(notification, "type", dto.type());
        FieldMapper.write(notification, "read", dto.read());
        FieldMapper.write(notification, "createdAt", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(notificationService.save(notification)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponseDto> update(@PathVariable UUID id, @RequestBody NotificationRequestDto dto) {
        Notification notification = new Notification();
        FieldMapper.write(notification, "user", userService.findById(dto.userId()));
        FieldMapper.write(notification, "ticket", dto.ticketId() == null ? null : ticketService.findById(dto.ticketId()));
        FieldMapper.write(notification, "title", dto.title());
        FieldMapper.write(notification, "message", dto.message());
        FieldMapper.write(notification, "type", dto.type());
        FieldMapper.write(notification, "read", dto.read());
        return ResponseEntity.ok(toResponseDto(notificationService.update(id, notification)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        notificationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private NotificationResponseDto toResponseDto(Notification notification) {
        User user = FieldMapper.read(notification, "user", User.class);
        Ticket ticket = FieldMapper.read(notification, "ticket", Ticket.class);
        return new NotificationResponseDto(
                FieldMapper.read(notification, "id", UUID.class),
                user == null ? null : FieldMapper.read(user, "id", UUID.class),
                ticket == null ? null : FieldMapper.read(ticket, "id", UUID.class),
                FieldMapper.read(notification, "title", String.class),
                FieldMapper.read(notification, "message", String.class),
                FieldMapper.read(notification, "type", com.helpdesk.model.enums.NotificationType.class),
                FieldMapper.read(notification, "read", Boolean.class),
                FieldMapper.read(notification, "createdAt", LocalDateTime.class)
        );
    }
}
