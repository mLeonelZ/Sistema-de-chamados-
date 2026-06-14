package com.helpdesk.controller;

import com.helpdesk.model.Ticket;
import com.helpdesk.service.TicketService;
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
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> findAll() {
        return ResponseEntity.ok(ticketService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ticketService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Ticket> create(@RequestBody Ticket ticket) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.save(ticket));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        ticketService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
