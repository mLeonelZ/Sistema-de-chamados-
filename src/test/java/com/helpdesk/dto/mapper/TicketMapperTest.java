package com.helpdesk.dto.mapper;

import com.helpdesk.dto.ticket.TicketRequestDto;
import com.helpdesk.dto.ticket.TicketResponseDto;
import com.helpdesk.model.Category;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;
import com.helpdesk.model.enums.TicketChannel;
import com.helpdesk.model.enums.TicketPriority;
import com.helpdesk.model.enums.TicketStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TicketMapperTest {

    @Test
    void toEntityShouldMapFields() {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        User client = new User();
        client.setId(UUID.randomUUID());
        User assignee = new User();
        assignee.setId(UUID.randomUUID());
        TicketRequestDto dto = new TicketRequestDto("T-1", "s", "d", category.getId(), TicketPriority.ALTA, TicketStatus.ABERTO, TicketChannel.EMAIL, client.getId(), assignee.getId(), null, null, null, null, null);
        Ticket ticket = TicketMapper.toEntity(dto, category, client, assignee);
        assertNotNull(ticket.getId());
        assertEquals("T-1", ticket.getCode());
        assertEquals(category, ticket.getCategory());
        assertEquals(client, ticket.getClient());
    }

    @Test
    void toResponseDtoShouldMapFields() {
        Category category = new Category();
        UUID categoryId = UUID.randomUUID();
        category.setId(categoryId);
        User client = new User();
        UUID clientId = UUID.randomUUID();
        client.setId(clientId);
        User assignee = new User();
        UUID assigneeId = UUID.randomUUID();
        assignee.setId(assigneeId);
        Ticket ticket = new Ticket();
        UUID id = UUID.randomUUID();
        ticket.setId(id);
        ticket.setCode("T-2");
        ticket.setSubject("sub");
        ticket.setDescription("desc");
        ticket.setCategory(category);
        ticket.setPriority(TicketPriority.MEDIA);
        ticket.setStatus(TicketStatus.EM_ANDAMENTO);
        ticket.setChannel(TicketChannel.PORTAL);
        ticket.setClient(client);
        ticket.setAssignee(assignee);
        ticket.setCreatedAt(LocalDateTime.now().minusDays(1));
        ticket.setUpdatedAt(LocalDateTime.now());
        TicketResponseDto response = TicketMapper.toResponse(ticket);
        assertEquals(id, response.id());
        assertEquals(categoryId, response.categoryId());
        assertEquals(clientId, response.clientId());
        assertEquals(assigneeId, response.assigneeId());
    }
}
