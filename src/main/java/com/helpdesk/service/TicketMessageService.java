package com.helpdesk.service;

import com.helpdesk.model.TicketMessage;
import com.helpdesk.repository.TicketMessageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TicketMessageService {

    private final TicketMessageRepository ticketMessageRepository;

    public TicketMessageService(TicketMessageRepository ticketMessageRepository) {
        this.ticketMessageRepository = ticketMessageRepository;
    }

    public List<TicketMessage> findAll() {
        return ticketMessageRepository.findAll();
    }

    public TicketMessage findById(UUID id) {
        return ticketMessageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ticket message not found"));
    }

    public TicketMessage save(TicketMessage ticketMessage) {
        return ticketMessageRepository.save(ticketMessage);
    }

    public void deleteById(UUID id) {
        ticketMessageRepository.deleteById(id);
    }
}
