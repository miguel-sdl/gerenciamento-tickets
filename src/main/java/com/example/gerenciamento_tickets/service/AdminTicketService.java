package com.example.gerenciamento_tickets.service;

import com.example.gerenciamento_tickets.dto.*;
import com.example.gerenciamento_tickets.exception.BadRequestException;
import com.example.gerenciamento_tickets.exception.NotFoundException;
import com.example.gerenciamento_tickets.mapper.CategoriaMapper;
import com.example.gerenciamento_tickets.mapper.TicketMapper;
import com.example.gerenciamento_tickets.model.*;
import com.example.gerenciamento_tickets.repository.CategoriaRepository;
import com.example.gerenciamento_tickets.repository.TicketRepository;
import com.example.gerenciamento_tickets.repository.UsuarioRepository;
import com.example.gerenciamento_tickets.specification.TicketSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminTicketService {

    private final TicketRepository ticketRepository;

    private final CategoriaRepository categoriaRepository;

    private final UsuarioRepository usuarioRepository;


    public CategoriaResponseBody criarCategoria(CriarCategoriaRequestBody dto) {
        categoriaRepository.findByNome(dto.nome()).ifPresent(categoria -> {
            throw new BadRequestException("Categoria " + categoria.getNome() + " ja existe");
        });


        List<Usuario> usuariosResponsaveis = usuarioRepository.findAllById(dto.usuariosResponsaveisIds());

        if (usuariosResponsaveis.stream().anyMatch(usuario -> usuario.getRole().equals(UserRole.USER))) {
            throw new BadRequestException("Nao e permitido que um usuario comum seja um usuario responsavel");
        }


        Categoria categoria = Categoria.builder()
                .nome(dto.nome())
                .prazoDefaultEmHoras(dto.prazoDefaultEmHoras())
                .usuariosResponsaveis(usuariosResponsaveis)
                .build();

        log.info("Criando categoria {}", categoria.getNome());
        return CategoriaMapper.INSTANCE.toCategoriaResponseBody(categoriaRepository.save(categoria));
    }

    public void atualizarCategoria(AtualizarCategoriaRequestBody dto) {
        Categoria categoria = categoriaRepository.findById(dto.id()).orElseThrow(() -> new NotFoundException("Categoria não encontrada"));

        if (dto.nome() != null) categoria.setNome(dto.nome());
        if (dto.prazoDefaultEmHoras() != null) categoria.setPrazoDefaultEmHoras(dto.prazoDefaultEmHoras());


        if (dto.usuariosResponsaveis() != null && !dto.usuariosResponsaveis().isEmpty()) {
            List<Usuario> usuariosResponsaveis = usuarioRepository.findAllById(dto.usuariosResponsaveis());

            if (usuariosResponsaveis.stream().anyMatch(usuario -> usuario.getRole().equals(UserRole.USER))) {
                throw new BadRequestException("Nao e permitido que um usuario comum seja um usuario responsavel");
            }

            categoria.setUsuariosResponsaveis(usuariosResponsaveis);
        }

        categoriaRepository.save(categoria);
        log.info("Atualizando categoria {}", categoria.getId());
    }

    public List<TicketResponseBody> filterTickets(TicketFilter filter) {
        log.info("Buscando por tickets utilizando TicketFilter");
        Specification<Ticket> vencidoSpecification;
        if (TicketStatus.RESOLVIDO.equals(filter.status())) {
            vencidoSpecification = TicketSpecification.isResolvidoAposPrazo(filter.vencido());
        } else {
            vencidoSpecification = TicketSpecification.isVencido(filter.vencido());
        }

        return ticketRepository.findAll(
                        vencidoSpecification
                                .and(TicketSpecification.hasUsuarioResponsavel(filter.usuarioId())
                                        .and(TicketSpecification.hasCategoria(filter.categoria())
                                                .and(TicketSpecification.hasStatus(filter.status())))))
                .stream().map(TicketMapper.INSTANCE::toTicketResponseBody).toList();
    }

    public void atualzarTicket(AtualizarTicketRequestBody dto) {
        Ticket ticket = ticketRepository.findById(dto.id()).orElseThrow(() -> new NotFoundException("Ticket não encontrado"));

        if (ticket.getStatus().equals(TicketStatus.RESOLVIDO)) {
            throw new BadRequestException("Nao e possivel atualizar um ticket resolvido");
        }

        if (dto.prazoParaAdicionar() != null) {
            LocalDateTime prazoAtual = ticket.getPrazoParaResolucao();
            ticket.setPrazoParaResolucao(prazoAtual.plusHours(dto.prazoParaAdicionar()));
        }

        if (dto.usuarioResponsavelID() != null) {
            Usuario usuarioResponsavel = usuarioRepository.findById(dto.usuarioResponsavelID()).orElseThrow(() -> new BadRequestException("Usuario Responsavel nao encontrado"));

            if (usuarioResponsavel.getRole().equals(UserRole.USER)) {
                throw new BadRequestException("Nao e permitido que um usuario comum seja responsavel por um ticket");
            }

            ticket.setUsuarioResponsavel(usuarioResponsavel);
        }
        log.info("Atualizando ticket {}", ticket.getId());
        ticketRepository.save(ticket);
    }
}
