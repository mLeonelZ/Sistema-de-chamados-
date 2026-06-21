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
        ticket.setSubject(dto.subject());
        ticket.setDescription(dto.description());
        ticket.setCategory(category);
        ticket.setPriority(dto.priority());
        ticket.setChannel(dto.channel());
        ticket.setClient(client);
        ticket.setAssignee(assignee);
        return ticket;
    }

    public static TicketResponseDto toResponse(Ticket ticket) {
        String slaFirstResponse = null;
        String slaResolution = null;
        LocalDateTime baseTime = ticket.getCreatedAt();
        if (baseTime != null && ticket.getSlaFirstResponseDeadline() != null && ticket.getSlaResolutionDeadline() != null) {
            long firstMins = java.time.Duration.between(baseTime, ticket.getSlaFirstResponseDeadline()).toMinutes();
            long resMins = java.time.Duration.between(baseTime, ticket.getSlaResolutionDeadline()).toMinutes();

            String policyName = null;
            if (ticket.getPriority() != null) {
                switch (ticket.getPriority()) {
                    case CRITICA:
                        policyName = "Crítico";
                        break;
                    case ALTA:
                        policyName = "Alto";
                        break;
                    case MEDIA:
                        policyName = "Médio";
                        break;
                    case BAIXA:
                        policyName = "Baixo";
                        break;
                }
            }
            if (policyName == null && ticket.getCategory() != null && ticket.getCategory().getSlaPolicy() != null) {
                policyName = ticket.getCategory().getSlaPolicy().getName();
            }
            if (policyName == null) {
                policyName = "Padrão";
            }

            if (firstMins >= 60) {
                slaFirstResponse = (firstMins / 60) + "h (SLA " + policyName + ")";
            } else {
                slaFirstResponse = firstMins + "m (SLA " + policyName + ")";
            }

            if (resMins >= 60) {
                slaResolution = (resMins / 60) + "h (SLA " + policyName + ")";
            } else {
                slaResolution = resMins + "m (SLA " + policyName + ")";
            }
        } else if (ticket.getCategory() != null && ticket.getCategory().getSlaPolicy() != null) {
            var policy = ticket.getCategory().getSlaPolicy();
            int firstMins = policy.getResponseTimeMinutes();
            if (firstMins >= 60) {
                slaFirstResponse = (firstMins / 60) + "h (SLA " + policy.getName() + ")";
            } else {
                slaFirstResponse = firstMins + "m (SLA " + policy.getName() + ")";
            }

            int resMins = policy.getResolutionTimeMinutes();
            if (resMins >= 60) {
                slaResolution = (resMins / 60) + "h (SLA " + policy.getName() + ")";
            } else {
                slaResolution = resMins + "m (SLA " + policy.getName() + ")";
            }
        }

        return TicketResponseDto.builder()
                .id(ticket.getId())
                .code(ticket.getCode())
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .categoryId(ticket.getCategory() != null ? ticket.getCategory().getId() : null)
                .category(ticket.getCategory() != null ? ticket.getCategory().getName() : null)
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .channel(ticket.getChannel())
                .clientId(ticket.getClient() != null ? ticket.getClient().getId() : null)
                .clientName(ticket.getClient() != null ? ticket.getClient().getName() : null)
                .assigneeId(ticket.getAssignee() != null ? ticket.getAssignee().getId() : null)
                .assigneeName(ticket.getAssignee() != null ? ticket.getAssignee().getName() : null)
                .slaFirstResponseDeadline(ticket.getSlaFirstResponseDeadline())
                .slaResolutionDeadline(ticket.getSlaResolutionDeadline())
                .firstResponseAt(ticket.getFirstResponseAt())
                .resolvedAt(ticket.getResolvedAt())
                .closedAt(ticket.getClosedAt())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .slaDeadline(ticket.getSlaResolutionDeadline())
                .slaFirstResponse(slaFirstResponse)
                .slaResolution(slaResolution)
                .build();
    }
}
