package com.helpdesk.controller;

import com.helpdesk.model.TicketMessage;
import com.helpdesk.service.TicketMessageService;
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
@RequestMapping("/api/v1/ticket-messages")
public class TicketMessageController {

    private final TicketMessageService ticketMessageService;

    public TicketMessageController(TicketMessageService ticketMessageService) {
        this.ticketMessageService = ticketMessageService;
    }

    @GetMapping
    public ResponseEntity<List<TicketMessage>> findAll() {
        return ResponseEntity.ok(ticketMessageService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketMessage> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ticketMessageService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TicketMessage> create(@RequestBody TicketMessage ticketMessage) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketMessageService.save(ticketMessage));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        ticketMessageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
