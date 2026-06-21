package com.helpdesk.service;

import com.helpdesk.exception.ResourceNotFoundException;
import com.helpdesk.model.Category;
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
    private final CategoryService categoryService;
    private final UserService userService;
    private final SlaPolicyService slaPolicyService;

    public TicketService(
            TicketRepository ticketRepository,
            NotificationService notificationService,
            CategoryService categoryService,
            UserService userService,
            SlaPolicyService slaPolicyService
    ) {
        this.ticketRepository = ticketRepository;
        this.notificationService = notificationService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.slaPolicyService = slaPolicyService;
    }

    @Transactional(readOnly = true)
    public List<Ticket> findAll() {
        return ticketRepository.findAllWithDetails();
    }

    @Transactional(readOnly = true)
    public List<Ticket> findAllByUser(User user) {
        if (user.getRole() == com.helpdesk.model.enums.Role.CLIENTE) {
            return ticketRepository.findByClientId(user.getId());
        } else {
            return ticketRepository.findAllWithDetails();
        }
    }

    @Transactional(readOnly = true)
    public Ticket findById(UUID id) {
        return ticketRepository.findByIdWithDetails(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
    }

    public Ticket findByCode(String code) {
        return ticketRepository.findByCode(code).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
    }

    @Transactional
    public Ticket save(Ticket ticket) {
        // Re-vincula entidades ao contexto de persistência atual para inicializar os Proxies Lazy
        if (ticket.getCategory() != null) {
            Category category = categoryService.findById(ticket.getCategory().getId());
            ticket.setCategory(category);
        }
        if (ticket.getClient() != null) {
            User client = userService.findById(ticket.getClient().getId());
            ticket.setClient(client);
        }
        if (ticket.getAssignee() != null) {
            User assignee = userService.findById(ticket.getAssignee().getId());
            ticket.setAssignee(assignee);
        }

        ticket.setId(UUID.randomUUID());
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setStatus(TicketStatus.ABERTO);
        
        long nextNumber = ticketRepository.count() + 1;
        ticket.setCode(String.format("CHM-%04d", nextNumber));

        calculateSlaDeadlines(ticket);

        Ticket saved = ticketRepository.save(ticket);
        
        // Notifica o cliente que o chamado foi aberto
        notificationService.send(
                saved.getClient(),
                saved,
                "Chamado Aberto: " + saved.getCode(),
                "Seu chamado foi recebido e está na fila de atendimento.",
                NotificationType.NOVO_CHAMADO
        );
        
        return findById(saved.getId());
    }

    @Transactional
    public Ticket update(UUID id, Ticket ticket) {
        Ticket existing = findById(id);
        
        User oldAssignee = existing.getAssignee();
        
        // Re-vincula entidades ao contexto de persistência atual
        Category category = null;
        if (ticket.getCategory() != null) {
            category = categoryService.findById(ticket.getCategory().getId());
        }
        User assignee = null;
        if (ticket.getAssignee() != null) {
            assignee = userService.findById(ticket.getAssignee().getId());
        }
        
        boolean priorityChanged = existing.getPriority() != ticket.getPriority();
        boolean categoryChanged = category != null && !category.equals(existing.getCategory());

        existing.setSubject(ticket.getSubject());
        existing.setDescription(ticket.getDescription());
        existing.setCategory(category);
        existing.setPriority(ticket.getPriority());
        existing.setChannel(ticket.getChannel());
        existing.setAssignee(assignee);
        existing.setUpdatedAt(LocalDateTime.now());
        
        if (priorityChanged || categoryChanged) {
            calculateSlaDeadlines(existing);
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

        return findById(updated.getId());
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
        
        return findById(saved.getId());
    }

    public void deleteById(UUID id) {
        ticketRepository.deleteById(id);
    }

    private void calculateSlaDeadlines(Ticket ticket) {
        if (ticket.getPriority() != null) {
            String policyName;
            switch (ticket.getPriority()) {
                case CRITICA:
                    policyName = "Crítico";
                    break;
                case ALTA:
                    policyName = "Alto";
                    break;
                case MEDIA:
                    policyName = "Médio";
                    break;
                case BAIXA:
                    policyName = "Baixo";
                    break;
                default:
                    return;
            }
            try {
                SlaPolicy sla = slaPolicyService.findByName(policyName);
                LocalDateTime baseTime = ticket.getCreatedAt() != null ? ticket.getCreatedAt() : LocalDateTime.now();
                ticket.setSlaFirstResponseDeadline(baseTime.plusMinutes(sla.getResponseTimeMinutes()));
                ticket.setSlaResolutionDeadline(baseTime.plusMinutes(sla.getResolutionTimeMinutes()));
            } catch (Exception e) {
                // fallback to category SLA if priority lookup fails
                if (ticket.getCategory() != null && ticket.getCategory().getSlaPolicy() != null) {
                    SlaPolicy sla = ticket.getCategory().getSlaPolicy();
                    LocalDateTime baseTime = ticket.getCreatedAt() != null ? ticket.getCreatedAt() : LocalDateTime.now();
                    ticket.setSlaFirstResponseDeadline(baseTime.plusMinutes(sla.getResponseTimeMinutes()));
                    ticket.setSlaResolutionDeadline(baseTime.plusMinutes(sla.getResolutionTimeMinutes()));
                }
            }
        } else if (ticket.getCategory() != null && ticket.getCategory().getSlaPolicy() != null) {
            SlaPolicy sla = ticket.getCategory().getSlaPolicy();
            LocalDateTime baseTime = ticket.getCreatedAt() != null ? ticket.getCreatedAt() : LocalDateTime.now();
            ticket.setSlaFirstResponseDeadline(baseTime.plusMinutes(sla.getResponseTimeMinutes()));
            ticket.setSlaResolutionDeadline(baseTime.plusMinutes(sla.getResolutionTimeMinutes()));
        }
    }
}
