package com.helpdesk.repository;

import com.helpdesk.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    @Query("SELECT t FROM Ticket t " +
           "LEFT JOIN FETCH t.category c " +
           "LEFT JOIN FETCH c.slaPolicy " +
           "LEFT JOIN FETCH t.client " +
           "LEFT JOIN FETCH t.assignee " +
           "WHERE t.code = :code")
    Optional<Ticket> findByCode(@Param("code") String code);

    @Query("SELECT t FROM Ticket t " +
           "LEFT JOIN FETCH t.category c " +
           "LEFT JOIN FETCH c.slaPolicy " +
           "LEFT JOIN FETCH t.client " +
           "LEFT JOIN FETCH t.assignee " +
           "WHERE t.client.id = :clientId")
    List<Ticket> findByClientId(@Param("clientId") UUID clientId);

    @Query("SELECT t FROM Ticket t " +
           "LEFT JOIN FETCH t.category c " +
           "LEFT JOIN FETCH c.slaPolicy " +
           "LEFT JOIN FETCH t.client " +
           "LEFT JOIN FETCH t.assignee")
    List<Ticket> findAllWithDetails();

    @Query("SELECT t FROM Ticket t " +
           "LEFT JOIN FETCH t.category c " +
           "LEFT JOIN FETCH c.slaPolicy " +
           "LEFT JOIN FETCH t.client " +
           "LEFT JOIN FETCH t.assignee " +
           "WHERE t.id = :id")
    Optional<Ticket> findByIdWithDetails(@Param("id") UUID id);
}
