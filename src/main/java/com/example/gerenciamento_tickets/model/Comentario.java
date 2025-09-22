package com.example.gerenciamento_tickets.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
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
