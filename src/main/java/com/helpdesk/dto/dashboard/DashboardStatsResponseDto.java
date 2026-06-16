package com.helpdesk.dto.dashboard;

import java.util.Map;

public record DashboardStatsResponseDto(
        long totalTickets,
        long openTickets,
        long inProgressTickets,
        long resolvedTickets,
        long closedTickets,
        Map<String, Long> ticketsByPriority,
        Map<String, Long> ticketsByCategory,
        Double averageResolutionTimeHours
) {
}
