package com.example.gerenciamento_tickets.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime criadoEm;

    @Column(columnDefinition = "TEXT")
    private String texto;

    @ManyToOne
    private Ticket ticket;

    @ManyToOne
    private Usuario autor;

}
