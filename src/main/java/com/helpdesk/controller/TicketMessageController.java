package com.helpdesk.controller;

import com.helpdesk.dto.mapper.TicketMessageMapper;
import com.helpdesk.dto.ticketmessage.TicketMessageRequestDto;
import com.helpdesk.dto.ticketmessage.TicketMessageResponseDto;
import com.helpdesk.model.TicketMessage;
import com.helpdesk.service.TicketMessageService;
import com.helpdesk.service.TicketService;
import com.helpdesk.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.ok(ticketMessageService.findAll().stream().map(TicketMessageMapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketMessageResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(TicketMessageMapper.toResponse(ticketMessageService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<TicketMessageResponseDto> create(@RequestBody TicketMessageRequestDto dto) {
        TicketMessage ticketMessage = TicketMessageMapper.toEntity(
                dto,
                ticketService.findById(dto.ticketId()),
                userService.findById(dto.authorId())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(TicketMessageMapper.toResponse(ticketMessageService.save(ticketMessage)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketMessageResponseDto> update(@PathVariable UUID id, @RequestBody TicketMessageRequestDto dto) {
        TicketMessage ticketMessage = TicketMessageMapper.toEntity(
                dto,
                ticketService.findById(dto.ticketId()),
                userService.findById(dto.authorId())
        );
        return ResponseEntity.ok(TicketMessageMapper.toResponse(ticketMessageService.update(id, ticketMessage)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        ticketMessageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
