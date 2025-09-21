package com.example.gerenciamento_tickets.service;

import com.example.gerenciamento_tickets.dto.CriarComentarioRequestBody;
import com.example.gerenciamento_tickets.dto.TicketResponseBody;
import com.example.gerenciamento_tickets.exception.BadRequestException;
import com.example.gerenciamento_tickets.exception.UnauthorizedException;
import com.example.gerenciamento_tickets.model.Categoria;
import com.example.gerenciamento_tickets.model.Ticket;
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

    @BeforeEach
    void setUp() {
        usuario = UsuarioCreator.usuario();
    }

    @Test
    void criarTicket_deveLancarExcecao_quandoCategoriaNaoExistir() {
        var request = TicketCreator.criarTicketRequestBody();

        when(categoriaRepository.findByNome(any())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> ticketService.criarTicket(request, usuario));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void criarTicket_deveLancarExcecao_quandoNaoExistiremTecnicosDisponiveis() {
        var request = TicketCreator.criarTicketRequestBody();

        when(categoriaRepository.findByNome(any())).thenReturn(Optional.of(CategoriaCreator.categoriaSemTecnicoDisponivel()));

        assertThrows(BadRequestException.class,
                () -> ticketService.criarTicket(request, usuario));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void criarTicket_deveCriarTicket_quandoCategoriaExisteETecnicoDisponivel() {
        var request = TicketCreator.criarTicketRequestBody();

        Categoria categoria = CategoriaCreator.validCategoria();


        when(categoriaRepository.findByNome(any())).thenReturn(Optional.of(categoria));
        when(ticketRepository.save(any())).thenReturn(TicketCreator.validTicket());

        TicketResponseBody response = ticketService.criarTicket(request, usuario);

        assertNotNull(response);
        assertEquals(request.titulo(), response.titulo());
        assertEquals(usuario.getUsername(), response.criadoPor());
        assertEquals(categoria.getUsuariosResponsaveis().getFirst().getUsername(), response.usuarioResponsavel());
        assertEquals(request.categoria(), response.categoria());
        assertTrue(response.prazoParaResolucao().isAfter(response.criadoEm()));
        assertNotNull(response.comentarios());
        assertEquals(1, response.comentarios().size());
        assertEquals(request.descricao(), response.comentarios().getFirst().texto());

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

        when(ticketRepository.findById(any())).thenReturn(Optional.of(TicketCreator.validTicket()));

        assertThrows(UnauthorizedException.class,
                () -> ticketService.adicionarComentario(request, outroUsuario));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void adicionarComentario_deveLancarExcecao_quandoTecnicoNaoForResponsavelDoTicket() {
        Usuario outroUsuario = UsuarioCreator.outroTecnico();
        CriarComentarioRequestBody request = ComentarioCreator.criarComentarioRequestBody();

        when(ticketRepository.findById(any())).thenReturn(Optional.of(TicketCreator.validTicket()));

        assertThrows(UnauthorizedException.class,
                () -> ticketService.adicionarComentario(request, outroUsuario));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void adicionarComentario_devePermitirComentario_quandoUsuarioForAdmin() {
        Usuario admin = UsuarioCreator.admin();

        CriarComentarioRequestBody request = ComentarioCreator.criarComentarioRequestBody();
        when(ticketRepository.findById(any())).thenReturn(Optional.of(TicketCreator.validTicket()));
        when(ticketRepository.save(any())).thenReturn(TicketCreator.validTicket());

        TicketResponseBody response = ticketService.adicionarComentario(request, admin);

        assertNotNull(response);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void adicionarComentario_devePermitirComentario_quandoUsuarioForCriadorDoTicket() {
        Usuario usuario = UsuarioCreator.usuario();

        CriarComentarioRequestBody request = ComentarioCreator.criarComentarioRequestBody();
        when(ticketRepository.findById(any())).thenReturn(Optional.of(TicketCreator.validTicket()));
        when(ticketRepository.save(any())).thenReturn(TicketCreator.validTicket());

        TicketResponseBody response = ticketService.adicionarComentario(request, usuario);

        assertNotNull(response);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void adicionarComentario_devePermitirComentario_quandoUsuarioForResponsavelDoTicket() {
        Usuario tecnico = UsuarioCreator.tecnico();

        CriarComentarioRequestBody request = ComentarioCreator.criarComentarioRequestBody();
        when(ticketRepository.findById(any())).thenReturn(Optional.of(TicketCreator.validTicket()));
        when(ticketRepository.save(any())).thenReturn(TicketCreator.validTicket());

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

        when(ticketRepository.findById(any())).thenReturn(Optional.of(TicketCreator.validTicket()));

        assertThrows(UnauthorizedException.class,
                () -> ticketService.findById(1, outroUsuario));
    }

    @Test
    void findById_deveLancarExcecao_quandoTecnicoNaoForResponsavelDoTicket() {
        Usuario outroTecnico = UsuarioCreator.outroTecnico();

        when(ticketRepository.findById(any())).thenReturn(Optional.of(TicketCreator.validTicket()));

        assertThrows(UnauthorizedException.class,
                () -> ticketService.findById(1, outroTecnico));
    }

    @Test
    void findById_deveRetornarTicket_quandoExistir() {

        when(ticketRepository.findById(any())).thenReturn(Optional.of(TicketCreator.validTicket()));

        TicketResponseBody response = ticketService.findById(1L, usuario);

        assertNotNull(response);
        assertEquals(TicketCreator.validTicket().getTitulo(), response.titulo());
    }

    @Test
    void findAll_deveRetornarTodosTickets_quandoUsuarioForAdmin() {
        Usuario admin = UsuarioCreator.admin();
        Ticket ticket = TicketCreator.validTicket();
        Ticket ticket2 = TicketCreator.validTicketComUsuarioComoCriadorEOutroTecnicoComoResponsavel();
        Ticket ticket3 = TicketCreator.validTicketComOutroUsuarioComoCriadorETecnicoComoResponsavel();
        Ticket ticket4 = TicketCreator.validTicketComTecnicoComoCriadorEOutroTecnicoComoResponsavel();


        when(ticketRepository.findAll()).thenReturn(List.of(ticket, ticket2, ticket3, ticket4));

        List<TicketResponseBody> response = ticketService.findAllTicketsByUser(admin);

        assertNotNull(response);
        assertEquals(4, response.size());
    }

    @Test
    void findAll_deveRetornarSomenteTicketsCriadosPeloUsuario_quandoUsuarioForRoleUSER() {
        Ticket ticket = TicketCreator.validTicket();
        Ticket ticket2 = TicketCreator.validTicketComUsuarioComoCriadorEOutroTecnicoComoResponsavel();


        when(ticketRepository.findByCriadoPor(any())).thenReturn(List.of(ticket, ticket2));

        List<TicketResponseBody> response = ticketService.findAllTicketsByUser(usuario);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(ticket.getId(), response.getFirst().id());

        verify(ticketRepository, never()).findByUsuarioResponsavel(any());
        verify(ticketRepository, never()).findAll();
    }


    @Test
    void findAll_deveRetonarTicketsQueEResponsavelETicketsCriados_quandoUsuarioForTecnico() {
        Usuario tecnico = UsuarioCreator.tecnico();

        Ticket ticket = TicketCreator.validTicket();
        Ticket ticket3 = TicketCreator.validTicketComOutroUsuarioComoCriadorETecnicoComoResponsavel();
        Ticket ticket4 = TicketCreator.validTicketComTecnicoComoCriadorEOutroTecnicoComoResponsavel();


        when(ticketRepository.findByUsuarioResponsavel(any())).thenReturn(List.of(ticket, ticket3));
        when(ticketRepository.findByCriadoPor(any())).thenReturn(List.of(ticket4));

        List<TicketResponseBody> response = ticketService.findAllTicketsByUser(tecnico);

        assertNotNull(response);
        assertEquals(3, response.size());

        verify(ticketRepository, never()).findAll();
    }
}