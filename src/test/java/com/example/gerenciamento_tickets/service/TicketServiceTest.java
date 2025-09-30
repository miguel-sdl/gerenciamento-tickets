package com.example.gerenciamento_tickets.service;

import com.example.gerenciamento_tickets.dto.CriarComentarioRequestBody;
import com.example.gerenciamento_tickets.dto.CriarTicketRequestBody;
import com.example.gerenciamento_tickets.dto.TicketResponseBody;
import com.example.gerenciamento_tickets.exception.BadRequestException;
import com.example.gerenciamento_tickets.exception.UnauthorizedException;
import com.example.gerenciamento_tickets.model.Categoria;
import com.example.gerenciamento_tickets.model.Ticket;
import com.example.gerenciamento_tickets.model.TicketStatus;
import com.example.gerenciamento_tickets.model.Usuario;
import com.example.gerenciamento_tickets.repository.CategoriaRepository;
import com.example.gerenciamento_tickets.repository.TicketRepository;
import com.example.gerenciamento_tickets.util.CategoriaCreator;
import com.example.gerenciamento_tickets.util.ComentarioCreator;
import com.example.gerenciamento_tickets.util.TicketCreator;
import com.example.gerenciamento_tickets.util.UsuarioCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;


    @Mock
    private TicketRepository ticketRepository;


    @InjectMocks
    private TicketService ticketService;

    private Usuario usuario;
    private Usuario tecnico;
    private Usuario admin;
    private Ticket validTicket;
    private CriarTicketRequestBody criarTicketRequestBody;

    @BeforeEach
    void setUp() {
        usuario = UsuarioCreator.usuario();
        tecnico = UsuarioCreator.tecnico();
        admin = UsuarioCreator.admin();
        validTicket = TicketCreator.validTicket();
        criarTicketRequestBody = TicketCreator.criarTicketRequestBody();
    }

    @Test
    void criarTicket_deveLancarExcecao_quandoCategoriaNaoExistir() {
        when(categoriaRepository.findByNome(any())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> ticketService.criarTicket(criarTicketRequestBody, usuario));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void criarTicket_deveLancarExcecao_quandoNaoExistiremTecnicosDisponiveis() {
        when(categoriaRepository.findByNome(any())).thenReturn(Optional.of(CategoriaCreator.categoriaSemTecnicoDisponivel()));

        assertThrows(BadRequestException.class,
                () -> ticketService.criarTicket(criarTicketRequestBody, usuario));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void criarTicket_deveCriarTicket_quandoCategoriaExisteETecnicoDisponivel() {
        Categoria categoria = CategoriaCreator.validCategoria();

        when(categoriaRepository.findByNome(any())).thenReturn(Optional.of(categoria));
        when(ticketRepository.save(any())).thenReturn(validTicket);

        TicketResponseBody response = ticketService.criarTicket(criarTicketRequestBody, usuario);

        assertNotNull(response);
        assertEquals(criarTicketRequestBody.titulo(), response.titulo());
        assertEquals(usuario.getUsername(), response.criadoPor());
        assertEquals(categoria.getUsuariosResponsaveis().getFirst().getUsername(), response.usuarioResponsavel());
        assertEquals(criarTicketRequestBody.categoria(), response.categoria());
        assertTrue(response.prazoParaResolucao().isAfter(response.criadoEm()));
        assertNotNull(response.comentarios());
        assertEquals(1, response.comentarios().size());
        assertEquals(criarTicketRequestBody.descricao(), response.comentarios().getFirst().texto());

        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void adicionarComentario_deveLancarExcecao_quandoTicketNaoExistir() {
        CriarComentarioRequestBody request = ComentarioCreator.criarComentarioRequestBody();
        when(ticketRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> ticketService.adicionarComentario(request, usuario));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void adicionarComentario_deveLancarExcecao_quandoUsuarioNaoForCriadorDoTicket() {
        Usuario outroUsuario = UsuarioCreator.outroUsuario();
        CriarComentarioRequestBody request = ComentarioCreator.criarComentarioRequestBody();

        when(ticketRepository.findById(any())).thenReturn(Optional.of(validTicket));

        assertThrows(UnauthorizedException.class,
                () -> ticketService.adicionarComentario(request, outroUsuario));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void adicionarComentario_deveLancarExcecao_quandoTecnicoNaoForResponsavelDoTicket() {
        Usuario outroUsuario = UsuarioCreator.outroTecnico();
        CriarComentarioRequestBody request = ComentarioCreator.criarComentarioRequestBody();

        when(ticketRepository.findById(any())).thenReturn(Optional.of(validTicket));

        assertThrows(UnauthorizedException.class,
                () -> ticketService.adicionarComentario(request, outroUsuario));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void adicionarComentario_devePermitirComentario_quandoUsuarioForAdmin() {
        CriarComentarioRequestBody request = ComentarioCreator.criarComentarioRequestBody();
        when(ticketRepository.findById(any())).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any())).thenReturn(validTicket);

        TicketResponseBody response = ticketService.adicionarComentario(request, admin);

        assertNotNull(response);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void adicionarComentario_devePermitirComentario_quandoUsuarioForCriadorDoTicket() {
        CriarComentarioRequestBody request = ComentarioCreator.criarComentarioRequestBody();
        when(ticketRepository.findById(any())).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any())).thenReturn(validTicket);

        TicketResponseBody response = ticketService.adicionarComentario(request, usuario);

        assertNotNull(response);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void adicionarComentario_devePermitirComentario_quandoUsuarioForResponsavelDoTicket() {
        CriarComentarioRequestBody request = ComentarioCreator.criarComentarioRequestBody();
        when(ticketRepository.findById(any())).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any())).thenReturn(validTicket);

        TicketResponseBody response = ticketService.adicionarComentario(request, tecnico);

        assertNotNull(response);
        verify(ticketRepository).save(any(Ticket.class));
    }


    @Test
    void findById_deveLancarExcecao_quandoTicketNaoExistir() {

        when(ticketRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> ticketService.findById(1, usuario));
    }

    @Test
    void findById_deveLancarExcecao_quandoUsuarioNaoForCriadorDoTicket() {
        Usuario outroUsuario = UsuarioCreator.outroUsuario();

        when(ticketRepository.findById(any())).thenReturn(Optional.of(validTicket));

        assertThrows(UnauthorizedException.class,
                () -> ticketService.findById(1, outroUsuario));
    }

    @Test
    void findById_deveLancarExcecao_quandoTecnicoNaoForResponsavelDoTicket() {
        Usuario outroTecnico = UsuarioCreator.outroTecnico();

        when(ticketRepository.findById(any())).thenReturn(Optional.of(validTicket));

        assertThrows(UnauthorizedException.class,
                () -> ticketService.findById(1, outroTecnico));
    }

    @Test
    void findById_deveRetornarTicket_quandoExistir() {

        when(ticketRepository.findById(any())).thenReturn(Optional.of(validTicket));

        TicketResponseBody response = ticketService.findById(1L, usuario);

        assertNotNull(response);
        assertEquals(TicketCreator.validTicket().getTitulo(), response.titulo());
    }

    @Test
    void findAll_deveRetornarTodosTickets_quandoUsuarioForAdmin() {
        Ticket ticket2 = TicketCreator.validTicketComUsuarioComoCriadorEOutroTecnicoComoResponsavel();
        Ticket ticket3 = TicketCreator.validTicketComOutroUsuarioComoCriadorETecnicoComoResponsavel();
        Ticket ticket4 = TicketCreator.validTicketComTecnicoComoCriadorEOutroTecnicoComoResponsavel();

        when(ticketRepository.findAll()).thenReturn(List.of(validTicket, ticket2, ticket3, ticket4));

        List<TicketResponseBody> response = ticketService.findAllTicketsByUser(admin);

        assertNotNull(response);
        assertEquals(4, response.size());
    }

    @Test
    void findAll_deveRetornarSomenteTicketsCriadosPeloUsuario_quandoUsuarioForRoleUSER() {
        Ticket ticket2 = TicketCreator.validTicketComUsuarioComoCriadorEOutroTecnicoComoResponsavel();

        when(ticketRepository.findByCriadoPor(any())).thenReturn(List.of(validTicket, ticket2));

        List<TicketResponseBody> response = ticketService.findAllTicketsByUser(usuario);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(validTicket.getId(), response.getFirst().id());

        verify(ticketRepository, never()).findByUsuarioResponsavel(any());
        verify(ticketRepository, never()).findAll();
    }


    @Test
    void findAll_deveRetonarTicketsQueEResponsavelETicketsCriados_quandoUsuarioForTecnico() {
        Ticket ticket2 = TicketCreator.validTicketComOutroUsuarioComoCriadorETecnicoComoResponsavel();
        Ticket ticket3 = TicketCreator.validTicketComTecnicoComoCriadorEOutroTecnicoComoResponsavel();


        when(ticketRepository.findByUsuarioResponsavel(any())).thenReturn(List.of(validTicket, ticket2));
        when(ticketRepository.findByCriadoPor(any())).thenReturn(List.of(ticket3));

        List<TicketResponseBody> response = ticketService.findAllTicketsByUser(tecnico);

        assertNotNull(response);
        assertEquals(3, response.size());

        verify(ticketRepository, never()).findAll();
    }


    @Test
    void resolverTicket_deveLancarExcecao_quandoNaoEncontraTicket() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> ticketService.resolverTicket(1L, usuario));

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void resolverTicket_deveLancarExcecao_quandoTicketJaEstaResolvido() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(TicketCreator.ticketResolvido()));

        assertThrows(BadRequestException.class, () -> ticketService.resolverTicket(1L, UsuarioCreator.tecnico()));

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void resolverTicket_deveLancarExcecao_quandoTecnicoNaoForResponsavelDoTicket() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(validTicket));

        assertThrows(UnauthorizedException.class, () -> ticketService.resolverTicket(1L, UsuarioCreator.outroTecnico()));

        verify(ticketRepository, never()).save(any(Ticket.class));
    }


    @Test
    void resolverTicket_deveResolverTicket_quandoTecnicoEResponsavelDoTicket() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any())).thenReturn(TicketCreator.ticketResolvido());

        TicketResponseBody response = ticketService.resolverTicket(1L, UsuarioCreator.tecnico());

        assertNotNull(response);
        assertEquals(TicketStatus.RESOLVIDO, response.status());

        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void resolverTicket_deveResolverTicket_quandoUsuarioEAdmin() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(validTicket));
        when(ticketRepository.save(any())).thenReturn(TicketCreator.ticketResolvido());

        TicketResponseBody response = ticketService.resolverTicket(1L, UsuarioCreator.admin());

        assertNotNull(response);
        assertEquals(TicketStatus.RESOLVIDO, response.status());

        verify(ticketRepository).save(any(Ticket.class));
    }

}