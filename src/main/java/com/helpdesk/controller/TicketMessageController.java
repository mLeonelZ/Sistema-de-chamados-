package com.helpdesk.controller;

import com.helpdesk.dto.mapper.TicketMessageMapper;
import com.helpdesk.dto.ticketmessage.TicketMessageRequestDto;
import com.helpdesk.dto.ticketmessage.TicketMessageResponseDto;
import com.helpdesk.model.TicketMessage;
import com.helpdesk.model.User;
import com.helpdesk.model.enums.Role;
import com.helpdesk.model.enums.MessageType;
import com.helpdesk.exception.UnauthorizedException;
import com.helpdesk.service.TicketMessageService;
import com.helpdesk.service.TicketService;
import com.helpdesk.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/api/v1/tickets/{ticketId}/messages")
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
    public ResponseEntity<List<TicketMessageResponseDto>> findByTicketId(
            @PathVariable UUID ticketId,
            Authentication authentication
    ) {
        // Busca o usuário logado para verificar a Role
        UUID userId = UUID.fromString(authentication.getName());
        User user = userService.findById(userId);
        
        // CLIENTE não vê mensagens INTERNAL
        boolean includeInternal = user.getRole() != Role.CLIENTE;
        
        List<TicketMessageResponseDto> messages = ticketMessageService.findByTicketId(ticketId, includeInternal)
                .stream()
                .map(TicketMessageMapper::toResponse)
                .toList();
                
        return ResponseEntity.ok(messages);
    }

    @PostMapping
    public ResponseEntity<TicketMessageResponseDto> create(
            @PathVariable UUID ticketId,
            @RequestBody @Valid TicketMessageRequestDto dto,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());
        User user = userService.findById(userId);

        if (dto.type() == MessageType.INTERNAL && user.getRole() == Role.CLIENTE) {
            throw new UnauthorizedException("Apenas administradores e atendentes podem enviar mensagens internas/privadas.");
        }

        TicketMessage ticketMessage = TicketMessageMapper.toEntity(
                dto,
                ticketService.findById(ticketId),
                userService.findById(dto.authorId())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(TicketMessageMapper.toResponse(ticketMessageService.save(ticketMessage)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID ticketId, @PathVariable UUID id) {
        ticketMessageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
