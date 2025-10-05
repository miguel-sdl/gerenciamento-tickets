package com.example.gerenciamento_tickets.repository;

import com.example.gerenciamento_tickets.model.*;
import com.example.gerenciamento_tickets.specification.TicketSpecification;
import com.example.gerenciamento_tickets.util.CategoriaCreator;
import com.example.gerenciamento_tickets.util.UsuarioCreator;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@Transactional
class TicketRepositoryTest {

    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    CategoriaRepository categoriaRepository;

    Usuario tecnico;
    Usuario outroTecnico;
    Categoria categoria;
    Categoria outraCategoria;

    @BeforeEach
    void setUp() {
        Usuario tecnicoParaSalvar = UsuarioCreator.tecnicoParaSalvar(new BCryptPasswordEncoder());
        tecnico = usuarioRepository.save(tecnicoParaSalvar);
        outroTecnico = Usuario.builder().username("outroTecnico").role(UserRole.TECNICO).build();
        outroTecnico = usuarioRepository.save(outroTecnico);


        categoria = CategoriaCreator.validCategoria();
        categoria.setUsuariosResponsaveis(List.of(tecnico, outroTecnico));
        categoria = categoriaRepository.save(categoria);
        outraCategoria = categoriaRepository.save(Categoria.builder().nome("Infra").usuariosResponsaveis(List.of(tecnico, outroTecnico)).build());


        // Salva ticket Aberto e dentro do prazo para resolver
        ticketRepository.save(Ticket.builder()
                .criadoEm(LocalDateTime.now())
                .prazoParaResolucao(LocalDateTime.now().plusHours(1))
                .usuarioResponsavel(tecnico)
                .categoria(categoria)
                .status(TicketStatus.ABERTO)
                .build());

        // Salva ticket Aberto, dentro do prazo para resolver, mas com outra categoria e tecnicoResponsavel
        ticketRepository.save(Ticket.builder()
                .usuarioResponsavel(outroTecnico)
                .status(TicketStatus.ABERTO)
                .criadoEm(LocalDateTime.now())
                .prazoParaResolucao(LocalDateTime.now().plusHours(1))
                .categoria(outraCategoria)
                .build());

        // Salva ticket Aberto mas ja passou do prazo de resolucao
        ticketRepository.save(Ticket.builder()
                .usuarioResponsavel(tecnico)
                .status(TicketStatus.ABERTO)
                .criadoEm(LocalDateTime.now().minusHours(2))
                .prazoParaResolucao(LocalDateTime.now().minusHours(1))
                .categoria(categoria)
                .build());

        // Salva ticket resolvido dentro do prazo de resolucao
        ticketRepository.save(Ticket.builder()
                .criadoEm(LocalDateTime.now())
                .prazoParaResolucao(LocalDateTime.now().plusHours(categoria.getPrazoDefaultEmHoras()))
                .usuarioResponsavel(tecnico)
                .categoria(categoria)
                .status(TicketStatus.RESOLVIDO)
                .build());

        // Salva ticket Resolvido apos o prazo de resolucao
        ticketRepository.save(Ticket.builder()
                .usuarioResponsavel(tecnico)
                .status(TicketStatus.RESOLVIDO)
                .criadoEm(LocalDateTime.now().minusHours(2))
                .prazoParaResolucao(LocalDateTime.now().minusHours(1))
                .resolvidoEm(LocalDateTime.now())
                .categoria(categoria)
                .build());


    }

    @Test
    void findAllSpecification_deveFiltrarPorCategoria() {
        Specification<Ticket> categoriaSpec = TicketSpecification.hasCategoria(categoria.getNome());
        Specification<Ticket> statusSpec = TicketSpecification.hasStatus(null);
        Specification<Ticket> usuarioSpec = TicketSpecification.hasUsuarioResponsavel(null);
        Specification<Ticket> vencidoSpec = TicketSpecification.isVencido(null);

        List<Ticket> tickets = ticketRepository.findAll(categoriaSpec.and(statusSpec).and(usuarioSpec).and(vencidoSpec));

        Assertions.assertNotNull(tickets);
        Assertions.assertEquals(4, tickets.size());
        Assertions.assertTrue(tickets.stream().allMatch(t -> t.getCategoria().getNome().equals(categoria.getNome())));
    }

    @Test
    void findAllSpecification_deveFiltrarPorStatus() {
        Specification<Ticket> categoriaSpec = TicketSpecification.hasCategoria(null);
        Specification<Ticket> statusSpec = TicketSpecification.hasStatus(TicketStatus.ABERTO);
        Specification<Ticket> usuarioSpec = TicketSpecification.hasUsuarioResponsavel(null);
        Specification<Ticket> vencidoSpec = TicketSpecification.isVencido(null);

        List<Ticket> tickets = ticketRepository.findAll(categoriaSpec.and(statusSpec).and(usuarioSpec).and(vencidoSpec));

        Assertions.assertNotNull(tickets);
        Assertions.assertEquals(3, tickets.size());
        Assertions.assertTrue(tickets.stream().allMatch(t -> t.getStatus().equals(TicketStatus.ABERTO)));
    }

    @Test
    void findAllSpecification_deveFiltrarPorUsuario() {
        Specification<Ticket> categoriaSpec = TicketSpecification.hasCategoria(null);
        Specification<Ticket> statusSpec = TicketSpecification.hasStatus(null);
        Specification<Ticket> usuarioSpec = TicketSpecification.hasUsuarioResponsavel(tecnico.getId());
        Specification<Ticket> vencidoSpec = TicketSpecification.isVencido(null);

        List<Ticket> tickets = ticketRepository.findAll(categoriaSpec.and(statusSpec).and(usuarioSpec).and(vencidoSpec));


        Assertions.assertNotNull(tickets);
        Assertions.assertEquals(4, tickets.size());
        Assertions.assertTrue(tickets.stream().allMatch(t -> t.getUsuarioResponsavel().equals(tecnico)));
    }

    @Test
    void findAllSpecification_deveFiltrarTicketsVencidos() {
        Specification<Ticket> categoriaSpec = TicketSpecification.hasCategoria(null);
        Specification<Ticket> statusSpec = TicketSpecification.hasStatus(null);
        Specification<Ticket> usuarioSpec = TicketSpecification.hasUsuarioResponsavel(null);
        Specification<Ticket> vencidoSpec = TicketSpecification.isVencido(true);

        List<Ticket> tickets = ticketRepository.findAll(categoriaSpec.and(statusSpec).and(usuarioSpec).and(vencidoSpec));

        Assertions.assertNotNull(tickets);
        Assertions.assertEquals(1, tickets.size());
        Assertions.assertTrue(tickets.stream().allMatch(t -> t.getPrazoParaResolucao().isBefore(LocalDateTime.now())));
    }

    @Test
    void findAllSpecification_deveFiltrarTicketsResolvidosAposPrazo() {
        Specification<Ticket> categoriaSpec = TicketSpecification.hasCategoria(null);
        Specification<Ticket> statusSpec = TicketSpecification.hasStatus(null);
        Specification<Ticket> usuarioSpec = TicketSpecification.hasUsuarioResponsavel(null);
        Specification<Ticket> vencidoSpec = TicketSpecification.isResolvidoAposPrazo(true);

        List<Ticket> tickets = ticketRepository.findAll(categoriaSpec.and(statusSpec).and(usuarioSpec).and(vencidoSpec));

        Assertions.assertNotNull(tickets);
        Assertions.assertEquals(1, tickets.size());
        Assertions.assertTrue(tickets.stream().allMatch(t -> t.getResolvidoEm().isAfter(t.getPrazoParaResolucao())));
    }

    @Test
    void findAllSpecification_devePermitirCombinarFiltros() {
        Specification<Ticket> categoriaSpec = TicketSpecification.hasCategoria(categoria.getNome());
        Specification<Ticket> statusSpec = TicketSpecification.hasStatus(TicketStatus.ABERTO);
        Specification<Ticket> usuarioSpec = TicketSpecification.hasUsuarioResponsavel(tecnico.getId());
        Specification<Ticket> vencidoSpec = TicketSpecification.isVencido(false);

        List<Ticket> tickets = ticketRepository.findAll(categoriaSpec.and(statusSpec).and(usuarioSpec).and(vencidoSpec));

        Assertions.assertNotNull(tickets);
        Assertions.assertEquals(1, tickets.size());
        Assertions.assertTrue(tickets.stream().allMatch(t -> t.getCategoria().getNome().equals(categoria.getNome())
                && t.getStatus().equals(TicketStatus.ABERTO) && t.getUsuarioResponsavel().equals(tecnico)
                && t.getPrazoParaResolucao().isAfter(LocalDateTime.now())));
    }

    @Test
    void findAllSpecifications_deveRetornarTodosTickets_quandoTodasSpecificationsSaoNull() {
        Specification<Ticket> categoriaSpec = TicketSpecification.hasCategoria(null);
        Specification<Ticket> statusSpec = TicketSpecification.hasStatus(null);
        Specification<Ticket> usuarioSpec = TicketSpecification.hasUsuarioResponsavel(null);
        Specification<Ticket> vencidoSpec = TicketSpecification.isVencido(null);

        List<Ticket> tickets = ticketRepository.findAll(categoriaSpec.and(statusSpec).and(usuarioSpec).and(vencidoSpec));

        Assertions.assertNotNull(tickets);
        Assertions.assertEquals(5, tickets.size());
    }


}