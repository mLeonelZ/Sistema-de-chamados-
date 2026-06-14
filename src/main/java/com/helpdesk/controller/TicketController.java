package com.helpdesk.controller;

import com.helpdesk.dto.FieldMapper;
import com.helpdesk.dto.ticket.TicketRequestDto;
import com.helpdesk.dto.ticket.TicketResponseDto;
import com.helpdesk.model.Category;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;
import com.helpdesk.service.CategoryService;
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
        return ResponseEntity.ok(ticketService.findAll().stream().map(this::toResponseDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponseDto(ticketService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<TicketResponseDto> create(@RequestBody TicketRequestDto dto) {
        Ticket ticket = new Ticket();
        FieldMapper.write(ticket, "id", dto.id());
        FieldMapper.write(ticket, "code", dto.code());
        FieldMapper.write(ticket, "subject", dto.subject());
        FieldMapper.write(ticket, "description", dto.description());
        FieldMapper.write(ticket, "category", categoryService.findById(dto.categoryId()));
        FieldMapper.write(ticket, "priority", dto.priority());
        FieldMapper.write(ticket, "status", dto.status());
        FieldMapper.write(ticket, "channel", dto.channel());
        FieldMapper.write(ticket, "client", userService.findById(dto.clientId()));
        FieldMapper.write(ticket, "assignee", dto.assigneeId() == null ? null : userService.findById(dto.assigneeId()));
        FieldMapper.write(ticket, "slaFirstResponseDeadline", dto.slaFirstResponseDeadline());
        FieldMapper.write(ticket, "slaResolutionDeadline", dto.slaResolutionDeadline());
        FieldMapper.write(ticket, "firstResponseAt", dto.firstResponseAt());
        FieldMapper.write(ticket, "resolvedAt", dto.resolvedAt());
        FieldMapper.write(ticket, "closedAt", dto.closedAt());
        FieldMapper.write(ticket, "createdAt", LocalDateTime.now());
        FieldMapper.write(ticket, "updatedAt", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(ticketService.save(ticket)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        ticketService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private TicketResponseDto toResponseDto(Ticket ticket) {
        Category category = FieldMapper.read(ticket, "category", Category.class);
        User client = FieldMapper.read(ticket, "client", User.class);
        User assignee = FieldMapper.read(ticket, "assignee", User.class);
        return new TicketResponseDto(
                FieldMapper.read(ticket, "id", UUID.class),
                FieldMapper.read(ticket, "code", String.class),
                FieldMapper.read(ticket, "subject", String.class),
                FieldMapper.read(ticket, "description", String.class),
                category == null ? null : FieldMapper.read(category, "id", UUID.class),
                FieldMapper.read(ticket, "priority", com.helpdesk.model.enums.TicketPriority.class),
                FieldMapper.read(ticket, "status", com.helpdesk.model.enums.TicketStatus.class),
                FieldMapper.read(ticket, "channel", com.helpdesk.model.enums.TicketChannel.class),
                client == null ? null : FieldMapper.read(client, "id", UUID.class),
                assignee == null ? null : FieldMapper.read(assignee, "id", UUID.class),
                FieldMapper.read(ticket, "slaFirstResponseDeadline", LocalDateTime.class),
                FieldMapper.read(ticket, "slaResolutionDeadline", LocalDateTime.class),
                FieldMapper.read(ticket, "firstResponseAt", LocalDateTime.class),
                FieldMapper.read(ticket, "resolvedAt", LocalDateTime.class),
                FieldMapper.read(ticket, "closedAt", LocalDateTime.class),
                FieldMapper.read(ticket, "createdAt", LocalDateTime.class),
                FieldMapper.read(ticket, "updatedAt", LocalDateTime.class)
        );
    }
}
