package com.example.gerenciamento_tickets.util;

import com.example.gerenciamento_tickets.dto.CriarComentarioRequestBody;
import com.example.gerenciamento_tickets.model.Comentario;
import com.example.gerenciamento_tickets.model.Ticket;

import java.time.LocalDateTime;

public class ComentarioCreator {
    public static Comentario comentario() {
        return Comentario.builder()
                .id(1)
                .criadoEm(LocalDateTime.now())
                .autor(UsuarioCreator.usuario())
                .texto("Não consigo logar")
                .ticket(Ticket.builder()
                        .id(1L)
                        .titulo("Erro de login")
                        .criadoEm(LocalDateTime.now())
                        .prazoParaResolucao(LocalDateTime.now().plusHours(24))
                        .criadoPor(UsuarioCreator.usuario())
                        .usuarioResponsavel(UsuarioCreator.tecnico()).build())
                .build();
    }

    public static CriarComentarioRequestBody criarComentarioRequestBody() {
        return new CriarComentarioRequestBody(1L, "Texto");
    }
}
