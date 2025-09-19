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
}