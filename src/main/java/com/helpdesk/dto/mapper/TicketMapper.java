package com.helpdesk.dto.mapper;

import com.helpdesk.dto.ticket.TicketRequestDto;
import com.helpdesk.dto.ticket.TicketResponseDto;
import com.helpdesk.model.Category;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class TicketMapper {

    public static Ticket toEntity(TicketRequestDto dto, Category category, User client, User assignee) {
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID());
        ticket.setCode(dto.code());
        ticket.setSubject(dto.subject());
        ticket.setDescription(dto.description());
        ticket.setCategory(category);
        ticket.setPriority(dto.priority());
        ticket.setStatus(dto.status());
        ticket.setChannel(dto.channel());
        ticket.setClient(client);
        ticket.setAssignee(assignee);
        ticket.setSlaFirstResponseDeadline(dto.slaFirstResponseDeadline());
        ticket.setSlaResolutionDeadline(dto.slaResolutionDeadline());
        ticket.setFirstResponseAt(dto.firstResponseAt());
        ticket.setResolvedAt(dto.resolvedAt());
        ticket.setClosedAt(dto.closedAt());
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        return ticket;
    }

    public static TicketResponseDto toResponse(Ticket ticket) {
        return TicketResponseDto.builder()
                .id(ticket.getId())
                .code(ticket.getCode())
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .categoryId(ticket.getCategory() != null ? ticket.getCategory().getId() : null)
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .channel(ticket.getChannel())
                .clientId(ticket.getClient() != null ? ticket.getClient().getId() : null)
                .assigneeId(ticket.getAssignee() != null ? ticket.getAssignee().getId() : null)
                .slaFirstResponseDeadline(ticket.getSlaFirstResponseDeadline())
                .slaResolutionDeadline(ticket.getSlaResolutionDeadline())
                .firstResponseAt(ticket.getFirstResponseAt())
                .resolvedAt(ticket.getResolvedAt())
                .closedAt(ticket.getClosedAt())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }
}
