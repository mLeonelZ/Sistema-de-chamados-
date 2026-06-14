package com.helpdesk.service;

import com.helpdesk.model.Notification;
import com.helpdesk.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    public Notification findById(UUID id) {
        return notificationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Notification not found"));
    }

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public void deleteById(UUID id) {
        notificationRepository.deleteById(id);
    }
}
