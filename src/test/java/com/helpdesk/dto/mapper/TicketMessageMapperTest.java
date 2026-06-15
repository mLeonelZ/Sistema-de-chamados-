package com.helpdesk.dto.mapper;

import com.helpdesk.dto.ticketmessage.TicketMessageRequestDto;
import com.helpdesk.dto.ticketmessage.TicketMessageResponseDto;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.TicketMessage;
import com.helpdesk.model.User;
import com.helpdesk.model.enums.MessageType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TicketMessageMapperTest {

    @Test
    void toEntityShouldMapFields() {
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID());
        User author = new User();
        author.setId(UUID.randomUUID());
        TicketMessageRequestDto dto = new TicketMessageRequestDto(ticket.getId(), author.getId(), MessageType.PUBLIC, "abc");
        TicketMessage message = TicketMessageMapper.toEntity(dto, ticket, author);
        assertNotNull(message.getId());
        assertEquals(ticket, message.getTicket());
        assertEquals(author, message.getAuthor());
        assertEquals("abc", message.getText());
    }

    @Test
    void toResponseDtoShouldMapFields() {
        Ticket ticket = new Ticket();
        UUID ticketId = UUID.randomUUID();
        ticket.setId(ticketId);
        User author = new User();
        UUID authorId = UUID.randomUUID();
        author.setId(authorId);
        TicketMessage message = new TicketMessage();
        UUID id = UUID.randomUUID();
        message.setId(id);
        message.setTicket(ticket);
        message.setAuthor(author);
        message.setType(MessageType.INTERNAL);
        message.setText("xyz");
        message.setCreatedAt(LocalDateTime.now());
        TicketMessageResponseDto response = TicketMessageMapper.toResponse(message);
        assertEquals(id, response.id());
        assertEquals(ticketId, response.ticketId());
        assertEquals(authorId, response.authorId());
    }
}
