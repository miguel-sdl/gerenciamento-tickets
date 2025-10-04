package com.example.gerenciamento_tickets.repository;

import com.example.gerenciamento_tickets.model.Ticket;
import com.example.gerenciamento_tickets.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {

    List<Ticket> findByCriadoPor(Usuario criadoPor);

    List<Ticket> findByUsuarioResponsavel(Usuario usuarioResponsavel);
}
