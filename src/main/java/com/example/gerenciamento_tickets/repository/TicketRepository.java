package com.example.gerenciamento_tickets.repository;

import com.example.gerenciamento_tickets.model.Ticket;
import com.example.gerenciamento_tickets.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByCriadoPor(Usuario criadoPor);

    List<Ticket> findByUsuarioResponsavel(Usuario usuarioResponsavel);
}
