package com.example.gerenciamento_tickets.repository;

import com.example.gerenciamento_tickets.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
