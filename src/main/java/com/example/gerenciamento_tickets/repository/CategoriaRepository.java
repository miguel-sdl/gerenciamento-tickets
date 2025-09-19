package com.example.gerenciamento_tickets.repository;

import com.example.gerenciamento_tickets.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNome(String categoria);
}
