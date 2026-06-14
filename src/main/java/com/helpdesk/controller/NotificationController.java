package com.helpdesk.controller;

import com.helpdesk.model.Notification;
import com.helpdesk.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> findAll() {
        return ResponseEntity.ok(notificationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Notification> create(@RequestBody Notification notification) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.save(notification));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        notificationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
