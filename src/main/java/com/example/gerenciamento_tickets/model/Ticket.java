package com.example.gerenciamento_tickets.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(100)")
    private TicketStatus status;

    private LocalDateTime criadoEm;

    private LocalDateTime prazoParaResolucao;

    private LocalDateTime resolvidoEm;

    @ManyToOne
    private Usuario criadoPor;

    @ManyToOne
    private Usuario usuarioResponsavel;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<Comentario> comentarios;

    @ManyToOne
    private Categoria categoria;

    public void adicionaComentario(Comentario comentario) {
        this.comentarios.add(comentario);
    }
}
