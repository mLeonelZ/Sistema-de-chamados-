package com.helpdesk.service;

import com.helpdesk.model.Ticket;
import com.helpdesk.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    public Ticket findById(UUID id) {
        return ticketRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
    }

    public Ticket findByCode(String code) {
        return ticketRepository.findByCode(code).orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
    }

    public Ticket save(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public void deleteById(UUID id) {
        ticketRepository.deleteById(id);
    }
}
