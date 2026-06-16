package com.helpdesk.service;

import com.helpdesk.dto.dashboard.DashboardStatsResponseDto;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.enums.TicketPriority;
import com.helpdesk.model.enums.TicketStatus;
import com.helpdesk.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final TicketRepository ticketRepository;

    public DashboardService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public DashboardStatsResponseDto getStats() {
        List<Ticket> tickets = ticketRepository.findAll();

        long total = tickets.size();
        long open = countByStatus(tickets, TicketStatus.ABERTO);
        long inProgress = countByStatus(tickets, TicketStatus.EM_ANDAMENTO);
        long resolved = countByStatus(tickets, TicketStatus.RESOLVIDO);
        long closed = countByStatus(tickets, TicketStatus.FECHADO);

        Map<String, Long> byPriority = tickets.stream()
                .collect(Collectors.groupingBy(t -> t.getPriority().name(), Collectors.counting()));

        Map<String, Long> byCategory = tickets.stream()
                .collect(Collectors.groupingBy(t -> t.getCategory().getName(), Collectors.counting()));

        Double avgResolutionTime = calculateAverageResolutionTime(tickets);

        return new DashboardStatsResponseDto(
                total, open, inProgress, resolved, closed,
                byPriority, byCategory, avgResolutionTime
        );
    }

    private long countByStatus(List<Ticket> tickets, TicketStatus status) {
        return tickets.stream().filter(t -> t.getStatus() == status).count();
    }

    private Double calculateAverageResolutionTime(List<Ticket> tickets) {
        List<Ticket> resolvedTickets = tickets.stream()
                .filter(t -> t.getResolvedAt() != null && t.getCreatedAt() != null)
                .toList();

        if (resolvedTickets.isEmpty()) {
            return 0.0;
        }

        long totalMinutes = resolvedTickets.stream()
                .mapToLong(t -> Duration.between(t.getCreatedAt(), t.getResolvedAt()).toMinutes())
                .sum();

        return (double) totalMinutes / resolvedTickets.size() / 60.0; // Em horas
    }
}
