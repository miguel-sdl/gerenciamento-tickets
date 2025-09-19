package com.example.gerenciamento_tickets.repository;

import com.example.gerenciamento_tickets.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
}
