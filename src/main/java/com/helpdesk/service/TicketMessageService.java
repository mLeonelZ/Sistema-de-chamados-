package com.helpdesk.service;

import com.helpdesk.exception.ResourceNotFoundException;
import com.helpdesk.model.TicketMessage;
import com.helpdesk.model.enums.MessageType;
import com.helpdesk.repository.TicketMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TicketMessageService {

    private final TicketMessageRepository ticketMessageRepository;
    private final NotificationService notificationService;

    public TicketMessageService(TicketMessageRepository ticketMessageRepository, NotificationService notificationService) {
        this.ticketMessageRepository = ticketMessageRepository;
        this.notificationService = notificationService;
    }

    public List<TicketMessage> findByTicketId(UUID ticketId, boolean includeInternal) {
        List<TicketMessage> messages = ticketMessageRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
        if (!includeInternal) {
            return messages.stream()
                    .filter(m -> m.getType() == MessageType.PUBLIC)
                    .toList();
        }
        return messages;
    }

    public TicketMessage findById(UUID id) {
        return ticketMessageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket message not found"));
    }

    @Transactional
    public TicketMessage save(TicketMessage ticketMessage) {
        ticketMessage.setId(UUID.randomUUID());
        ticketMessage.setCreatedAt(LocalDateTime.now());
        TicketMessage saved = ticketMessageRepository.save(ticketMessage);
        
        // Notifica o destinatário (Se o autor for o cliente, notifica o técnico. Se for o técnico, notifica o cliente)
        if (saved.getType() == MessageType.PUBLIC) {
            com.helpdesk.model.Ticket ticket = saved.getTicket();
            com.helpdesk.model.User author = saved.getAuthor();
            com.helpdesk.model.User recipient = author.equals(ticket.getClient()) ? ticket.getAssignee() : ticket.getClient();
            
            if (recipient != null) {
                notificationService.send(
                        recipient,
                        ticket,
                        "Nova Mensagem: " + ticket.getCode(),
                        "Você recebeu uma nova mensagem no chamado.",
                        com.helpdesk.model.enums.NotificationType.NOVA_MENSAGEM
                );
            }
        }
        
        return saved;
    }

    public void deleteById(UUID id) {
        ticketMessageRepository.deleteById(id);
    }
}
