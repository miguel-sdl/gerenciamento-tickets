package com.example.gerenciamento_tickets.repository;

import com.example.gerenciamento_tickets.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
