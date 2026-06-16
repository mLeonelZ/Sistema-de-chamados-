package com.helpdesk.service;

import com.helpdesk.exception.ResourceNotFoundException;
import com.helpdesk.model.SlaPolicy;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;
import com.helpdesk.model.enums.NotificationType;
import com.helpdesk.model.enums.TicketStatus;
import com.helpdesk.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final NotificationService notificationService;

    public TicketService(TicketRepository ticketRepository, NotificationService notificationService) {
        this.ticketRepository = ticketRepository;
        this.notificationService = notificationService;
    }

    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    public Ticket findById(UUID id) {
        return ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
    }

    public Ticket findByCode(String code) {
        return ticketRepository.findByCode(code).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
    }

    @Transactional
    public Ticket save(Ticket ticket) {
        ticket.setId(UUID.randomUUID());
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setStatus(TicketStatus.ABERTO);
        
        long nextNumber = ticketRepository.count() + 1;
        ticket.setCode(String.format("CHM-%04d", nextNumber));

        if (ticket.getCategory() != null && ticket.getCategory().getSlaPolicy() != null) {
            SlaPolicy sla = ticket.getCategory().getSlaPolicy();
            ticket.setSlaFirstResponseDeadline(ticket.getCreatedAt().plusMinutes(sla.getResponseTimeMinutes()));
            ticket.setSlaResolutionDeadline(ticket.getCreatedAt().plusMinutes(sla.getResolutionTimeMinutes()));
        }

        Ticket saved = ticketRepository.save(ticket);
        
        // Notifica o cliente que o chamado foi aberto
        notificationService.send(
                saved.getClient(),
                saved,
                "Chamado Aberto: " + saved.getCode(),
                "Seu chamado foi recebido e está na fila de atendimento.",
                NotificationType.NOVO_CHAMADO
        );
        
        return saved;
    }

    @Transactional
    public Ticket update(UUID id, Ticket ticket) {
        Ticket existing = findById(id);
        
        User oldAssignee = existing.getAssignee();
        
        existing.setSubject(ticket.getSubject());
        existing.setDescription(ticket.getDescription());
        existing.setCategory(ticket.getCategory());
        existing.setPriority(ticket.getPriority());
        existing.setChannel(ticket.getChannel());
        existing.setAssignee(ticket.getAssignee());
        existing.setUpdatedAt(LocalDateTime.now());
        
        // Se a categoria mudou, recalcula SLA
        if (ticket.getCategory() != null && !ticket.getCategory().equals(existing.getCategory())) {
            SlaPolicy sla = ticket.getCategory().getSlaPolicy();
            existing.setSlaFirstResponseDeadline(existing.getCreatedAt().plusMinutes(sla.getResponseTimeMinutes()));
            existing.setSlaResolutionDeadline(existing.getCreatedAt().plusMinutes(sla.getResolutionTimeMinutes()));
        }

        Ticket updated = ticketRepository.save(existing);
        
        // Notifica novo técnico atribuído
        if (updated.getAssignee() != null && !updated.getAssignee().equals(oldAssignee)) {
            notificationService.send(
                    updated.getAssignee(),
                    updated,
                    "Novo Chamado Atribuído",
                    "O chamado " + updated.getCode() + " foi atribuído a você.",
                    NotificationType.ATRIBUICAO
            );
        }

        return updated;
    }

    @Transactional
    public Ticket updateStatus(UUID id, TicketStatus status) {
        Ticket ticket = findById(id);
        TicketStatus oldStatus = ticket.getStatus();
        ticket.setStatus(status);
        ticket.setUpdatedAt(LocalDateTime.now());
        
        if (status == TicketStatus.RESOLVIDO) {
            ticket.setResolvedAt(LocalDateTime.now());
        } else if (status == TicketStatus.FECHADO) {
            ticket.setClosedAt(LocalDateTime.now());
        }
        
        Ticket saved = ticketRepository.save(ticket);
        
        // Notifica o cliente sobre a mudança de status
        if (status != oldStatus) {
            notificationService.send(
                    saved.getClient(),
                    saved,
                    "Status Atualizado: " + saved.getCode(),
                    "O status do seu chamado mudou para " + status,
                    NotificationType.INFO
            );
        }
        
        return saved;
    }

    public void deleteById(UUID id) {
        ticketRepository.deleteById(id);
    }
}
