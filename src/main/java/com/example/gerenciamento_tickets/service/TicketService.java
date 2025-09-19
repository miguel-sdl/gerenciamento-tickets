package com.example.gerenciamento_tickets.service;

import com.example.gerenciamento_tickets.dto.CriarTicketRequestBody;
import com.example.gerenciamento_tickets.dto.TicketResponseBody;
import com.example.gerenciamento_tickets.exception.BadRequestException;
import com.example.gerenciamento_tickets.mapper.TicketMapper;
import com.example.gerenciamento_tickets.model.*;
import com.example.gerenciamento_tickets.repository.CategoriaRepository;
import com.example.gerenciamento_tickets.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    private final CategoriaRepository categoriaRepository;

    public TicketResponseBody criarTicket(CriarTicketRequestBody dto, Usuario usuario) {

        Categoria categoria = categoriaRepository.findByNome(dto.categoria()).orElseThrow(() -> new BadRequestException("Categoria " + dto.categoria() + " não encontrada"));

        Usuario usuarioResponsavel = categoria.getUsuariosResponsaveis().stream().findAny().orElseThrow(() -> new BadRequestException("Nenhum tecnico encontrado para a categoria selecionada"));


        LocalDateTime now = LocalDateTime.now();

        Comentario primeiroComentario = Comentario.builder()
                .autor(usuario)
                .texto(dto.descricao())
                .criadoEm(now).build();

        Ticket ticket = Ticket.builder()
                .titulo(dto.titulo())
                .comentarios(List.of(primeiroComentario))
                .status(TicketStatus.ABERTO)
                .criadoPor(usuario)
                .usuarioResponsavel(usuarioResponsavel)
                .categoria(categoria)
                .criadoEm(now)
                .prazoParaResolucao(now.plusHours(categoria.getPrazoDefaultEmHoras())).build();

        log.info("Salvando Ticket {}, criado por usuario {}", ticket.getTitulo(), usuario.getId());
        return TicketMapper.INSTANCE.toTicketResponseBody(ticketRepository.save(ticket));
    }
}
