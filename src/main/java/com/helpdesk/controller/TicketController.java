package com.helpdesk.controller;

import com.helpdesk.dto.mapper.TicketMapper;
import com.helpdesk.dto.ticket.TicketRequestDto;
import com.helpdesk.dto.ticket.TicketResponseDto;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.enums.TicketStatus;
import com.helpdesk.service.CategoryService;
import com.helpdesk.service.TicketService;
import com.helpdesk.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final CategoryService categoryService;
    private final UserService userService;

    public TicketController(TicketService ticketService, CategoryService categoryService, UserService userService) {
        this.ticketService = ticketService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseDto>> findAll() {
        return ResponseEntity.ok(ticketService.findAll().stream().map(TicketMapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(TicketMapper.toResponse(ticketService.findById(id)));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<TicketResponseDto> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(TicketMapper.toResponse(ticketService.findByCode(code)));
    }

    @PostMapping
    public ResponseEntity<TicketResponseDto> create(@RequestBody @Valid TicketRequestDto dto) {
        Ticket ticket = TicketMapper.toEntity(
                dto,
                categoryService.findById(dto.categoryId()),
                userService.findById(dto.clientId()),
                dto.assigneeId() == null ? null : userService.findById(dto.assigneeId())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(TicketMapper.toResponse(ticketService.save(ticket)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponseDto> update(@PathVariable UUID id, @RequestBody @Valid TicketRequestDto dto) {
        Ticket ticket = TicketMapper.toEntity(
                dto,
                categoryService.findById(dto.categoryId()),
                userService.findById(dto.clientId()),
                dto.assigneeId() == null ? null : userService.findById(dto.assigneeId())
        );
        return ResponseEntity.ok(TicketMapper.toResponse(ticketService.update(id, ticket)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponseDto> updateStatus(@PathVariable UUID id, @RequestParam TicketStatus status) {
        return ResponseEntity.ok(TicketMapper.toResponse(ticketService.updateStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        ticketService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
