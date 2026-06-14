package com.helpdesk.repository;

import com.helpdesk.model.TicketMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TicketMessageRepository extends JpaRepository<TicketMessage, UUID> {
}
