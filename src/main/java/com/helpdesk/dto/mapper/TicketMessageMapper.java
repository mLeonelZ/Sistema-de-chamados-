package com.helpdesk.dto.mapper;

import com.helpdesk.dto.ticketmessage.TicketMessageRequestDto;
import com.helpdesk.dto.ticketmessage.TicketMessageResponseDto;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.TicketMessage;
import com.helpdesk.model.User;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class TicketMessageMapper {

    public static TicketMessage toEntity(TicketMessageRequestDto dto, Ticket ticket, User author) {
        TicketMessage ticketMessage = new TicketMessage();
        ticketMessage.setId(UUID.randomUUID());
        ticketMessage.setTicket(ticket);
        ticketMessage.setAuthor(author);
        ticketMessage.setType(dto.type());
        ticketMessage.setText(dto.text());
        ticketMessage.setCreatedAt(LocalDateTime.now());
        return ticketMessage;
    }

    public static TicketMessageResponseDto toResponse(TicketMessage ticketMessage) {
        return TicketMessageResponseDto.builder()
                .id(ticketMessage.getId())
                .ticketId(ticketMessage.getTicket() != null ? ticketMessage.getTicket().getId() : null)
                .authorId(ticketMessage.getAuthor() != null ? ticketMessage.getAuthor().getId() : null)
                .type(ticketMessage.getType())
                .text(ticketMessage.getText())
                .createdAt(ticketMessage.getCreatedAt())
                .build();
    }
}
