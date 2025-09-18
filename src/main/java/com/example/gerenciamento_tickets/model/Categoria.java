package com.example.gerenciamento_tickets.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Entity
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nome;

    private int prazoDefaultEmHoras;

    @ManyToMany
    private List<Usuario> usuariosResponsaveis;

}
