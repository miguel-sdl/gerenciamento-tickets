package com.example.gerenciamento_tickets.util;

import com.example.gerenciamento_tickets.dto.AtualizarTicketRequestBody;
import com.example.gerenciamento_tickets.dto.CriarTicketRequestBody;
import com.example.gerenciamento_tickets.model.Categoria;
import com.example.gerenciamento_tickets.model.Ticket;
import com.example.gerenciamento_tickets.model.TicketStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TicketCreator {

    private static final String TITULO = "Erro de login";
    private static final String DESCRICAO = "Não consigo logar";
    private static final String CATEGORIA = "Suporte";

    public static Ticket validTicket() {
        Categoria categoria = CategoriaCreator.validCategoria();
        return Ticket.builder()
                .id(1L)
                .titulo(TITULO)
                .criadoEm(LocalDateTime.now())
                .prazoParaResolucao(LocalDateTime.now().plusHours(categoria.getPrazoDefaultEmHoras()))
                .criadoPor(UsuarioCreator.usuario())
                .usuarioResponsavel(UsuarioCreator.tecnico())
                .comentarios(new ArrayList<>(List.of(ComentarioCreator.comentario())))
                .categoria(categoria)
                .status(TicketStatus.ABERTO)
                .build();
    }

    public static Ticket ticketResolvido() {
        Categoria categoria = CategoriaCreator.validCategoria();
        return Ticket.builder()
                .id(1L)
                .titulo(TITULO)
                .criadoEm(LocalDateTime.now())
                .prazoParaResolucao(LocalDateTime.now().plusHours(categoria.getPrazoDefaultEmHoras()))
                .criadoPor(UsuarioCreator.usuario())
                .usuarioResponsavel(UsuarioCreator.tecnico())
                .comentarios(new ArrayList<>(List.of(ComentarioCreator.comentario())))
                .categoria(categoria)
                .status(TicketStatus.RESOLVIDO)
                .build();
    }

    public static Ticket validTicketComOutroUsuarioComoCriadorETecnicoComoResponsavel() {
        Categoria categoria = CategoriaCreator.validCategoria();
        return Ticket.builder()
                .id(1L)
                .titulo(TITULO)
                .criadoEm(LocalDateTime.now())
                .prazoParaResolucao(LocalDateTime.now().plusHours(categoria.getPrazoDefaultEmHoras()))
                .criadoPor(UsuarioCreator.outroUsuario())
                .usuarioResponsavel(UsuarioCreator.tecnico())
                .comentarios(new ArrayList<>(List.of(ComentarioCreator.comentario())))
                .categoria(categoria)
                .status(TicketStatus.ABERTO)
                .build();
    }

    public static Ticket validTicketComUsuarioComoCriadorEOutroTecnicoComoResponsavel() {
        Categoria categoria = CategoriaCreator.validCategoria();
        return Ticket.builder()
                .id(1L)
                .titulo(TITULO)
                .criadoEm(LocalDateTime.now())
                .prazoParaResolucao(LocalDateTime.now().plusHours(categoria.getPrazoDefaultEmHoras()))
                .criadoPor(UsuarioCreator.usuario())
                .usuarioResponsavel(UsuarioCreator.outroTecnico())
                .comentarios(new ArrayList<>(List.of(ComentarioCreator.comentario())))
                .categoria(categoria)
                .status(TicketStatus.ABERTO)
                .build();
    }

    public static Ticket validTicketComTecnicoComoCriadorEOutroTecnicoComoResponsavel() {
        Categoria categoria = CategoriaCreator.validCategoria();
        return Ticket.builder()
                .id(1L)
                .titulo(TITULO)
                .criadoEm(LocalDateTime.now())
                .prazoParaResolucao(LocalDateTime.now().plusHours(categoria.getPrazoDefaultEmHoras()))
                .criadoPor(UsuarioCreator.usuario())
                .usuarioResponsavel(UsuarioCreator.outroTecnico())
                .comentarios(new ArrayList<>(List.of(ComentarioCreator.comentario())))
                .categoria(categoria)
                .status(TicketStatus.ABERTO)
                .build();
    }

    public static CriarTicketRequestBody criarTicketRequestBody() {
        return new CriarTicketRequestBody(TITULO, DESCRICAO, CATEGORIA);
    }

    public static AtualizarTicketRequestBody atualizarTicketRequestBody() {
        return new AtualizarTicketRequestBody(1L, 10, 1L);
    }
}
