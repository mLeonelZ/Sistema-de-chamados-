package com.helpdesk.controller;

import com.helpdesk.dto.FieldMapper;
import com.helpdesk.dto.ticketmessage.TicketMessageRequestDto;
import com.helpdesk.dto.ticketmessage.TicketMessageResponseDto;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.TicketMessage;
import com.helpdesk.model.User;
import com.helpdesk.service.TicketMessageService;
import com.helpdesk.service.TicketService;
import com.helpdesk.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ticket-messages")
public class TicketMessageController {

    private final TicketMessageService ticketMessageService;
    private final TicketService ticketService;
    private final UserService userService;

    public TicketMessageController(TicketMessageService ticketMessageService, TicketService ticketService, UserService userService) {
        this.ticketMessageService = ticketMessageService;
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<TicketMessageResponseDto>> findAll() {
        return ResponseEntity.ok(ticketMessageService.findAll().stream().map(this::toResponseDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketMessageResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponseDto(ticketMessageService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<TicketMessageResponseDto> create(@RequestBody TicketMessageRequestDto dto) {
        TicketMessage ticketMessage = new TicketMessage();
        FieldMapper.write(ticketMessage, "id", dto.id());
        FieldMapper.write(ticketMessage, "ticket", ticketService.findById(dto.ticketId()));
        FieldMapper.write(ticketMessage, "author", userService.findById(dto.authorId()));
        FieldMapper.write(ticketMessage, "type", dto.type());
        FieldMapper.write(ticketMessage, "text", dto.text());
        FieldMapper.write(ticketMessage, "createdAt", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(ticketMessageService.save(ticketMessage)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        ticketMessageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private TicketMessageResponseDto toResponseDto(TicketMessage ticketMessage) {
        Ticket ticket = FieldMapper.read(ticketMessage, "ticket", Ticket.class);
        User author = FieldMapper.read(ticketMessage, "author", User.class);
        return new TicketMessageResponseDto(
                FieldMapper.read(ticketMessage, "id", UUID.class),
                ticket == null ? null : FieldMapper.read(ticket, "id", UUID.class),
                author == null ? null : FieldMapper.read(author, "id", UUID.class),
                FieldMapper.read(ticketMessage, "type", com.helpdesk.model.enums.MessageType.class),
                FieldMapper.read(ticketMessage, "text", String.class),
                FieldMapper.read(ticketMessage, "createdAt", LocalDateTime.class)
        );
    }
}
