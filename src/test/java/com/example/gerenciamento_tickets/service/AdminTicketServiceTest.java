package com.example.gerenciamento_tickets.service;

import com.example.gerenciamento_tickets.dto.CategoriaResponseBody;
import com.example.gerenciamento_tickets.exception.BadRequestException;
import com.example.gerenciamento_tickets.exception.NotFoundException;
import com.example.gerenciamento_tickets.model.Categoria;
import com.example.gerenciamento_tickets.model.Ticket;
import com.example.gerenciamento_tickets.repository.CategoriaRepository;
import com.example.gerenciamento_tickets.repository.TicketRepository;
import com.example.gerenciamento_tickets.repository.UsuarioRepository;
import com.example.gerenciamento_tickets.util.CategoriaCreator;
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
class AdminTicketServiceTest {

    @InjectMocks
    private AdminTicketService adminTicketService;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TicketRepository ticketRepository;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = CategoriaCreator.validCategoria();
    }


    @Test
    void criarCategoria_deveLancarExcecao_quandoJaExisteUmaCategoriaComMesmoNome() {
        when(categoriaRepository.findByNome(anyString())).thenReturn(Optional.of(categoria));

        assertThrows(BadRequestException.class,
                () -> adminTicketService.criarCategoria(CategoriaCreator.criarCategoriaRequestBody()));

        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void criarCategoria_deveLancarExcecao_quandoAlgumUsuarioForRoleUser() {
        when(categoriaRepository.findByNome(anyString())).thenReturn(Optional.empty());

        when(usuarioRepository.findAllById(any())).thenReturn(List.of(UsuarioCreator.usuario()));

        assertThrows(BadRequestException.class,
                () -> adminTicketService.criarCategoria(CategoriaCreator.criarCategoriaRequestBody()));

        verify(categoriaRepository, never()).save(any());

    }

    @Test
    void criarCategoria_deveCriarCategoria_quandoNaoExisteCategoriaComMesmoNomeENaoRecebeIdDeUsuarioRoleUser() {
        when(categoriaRepository.findByNome(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findAllById(any())).thenReturn(List.of(UsuarioCreator.tecnico()));
        when(categoriaRepository.save(any())).thenReturn(CategoriaCreator.validCategoria());

        CategoriaResponseBody response = adminTicketService.criarCategoria(CategoriaCreator.criarCategoriaRequestBody());

        assertNotNull(response);
        verify(categoriaRepository).save(any(Categoria.class));

    }

    @Test
    void atualizarCategoria_deveLancarExcecao_quandoNaoExisteEncontraCategoriaParaAtualizar() {
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> adminTicketService.atualizarCategoria(CategoriaCreator.atualizarCategoriaRequestBody()));

        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void atualizarCategoria_deveLancarExcecao_quandoAlgumUsuarioForRoleUser() {
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria));

        when(usuarioRepository.findAllById(any())).thenReturn(List.of(UsuarioCreator.usuario()));

        assertThrows(BadRequestException.class,
                () -> adminTicketService.atualizarCategoria(CategoriaCreator.atualizarCategoriaRequestBody()));

        verify(categoriaRepository, never()).save(any());

    }

    @Test
    void atualizarCategoria_deveAtualizarCategoria_quandoEncontraCategoriaENaoRecebeIdDeUsuarioRoleUser() {
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria));
        when(usuarioRepository.findAllById(any())).thenReturn(List.of(UsuarioCreator.tecnico()));

        assertDoesNotThrow(() -> adminTicketService.atualizarCategoria(CategoriaCreator.atualizarCategoriaRequestBody()));

        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void atualizarTicket_deveLancarExcecao_quandoNaoEncontraTicket() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> adminTicketService.atualzarTicket(TicketCreator.atualizarTicketRequestBody()));

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void atualizarTicket_deveLancarExcecao_quandoNaoEncontraTecnico() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(TicketCreator.validTicket()));
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> adminTicketService.atualzarTicket(TicketCreator.atualizarTicketRequestBody()));

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void atualizarTicket_deveLancarExcecao_quandoTicketJaResolvido() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(TicketCreator.ticketResolvido()));

        assertThrows(BadRequestException.class, () -> adminTicketService.atualzarTicket(TicketCreator.atualizarTicketRequestBody()));

        verify(ticketRepository, never()).save(any(Ticket.class));
    }


    @Test
    void atualizarTicket_deveLancarExcecao_uandoAlgumUsuarioForRoleUser() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(TicketCreator.validTicket()));
        when(usuarioRepository.findById(any())).thenReturn(Optional.of(UsuarioCreator.usuario()));

        assertThrows(BadRequestException.class,
                () -> adminTicketService.atualzarTicket(TicketCreator.atualizarTicketRequestBody()));

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void atualizarTicket_deveAtualizarTicket() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(TicketCreator.validTicket()));
        when(usuarioRepository.findById(any())).thenReturn(Optional.of(UsuarioCreator.tecnico()));

        assertDoesNotThrow(() -> adminTicketService.atualzarTicket(TicketCreator.atualizarTicketRequestBody()));

        verify(ticketRepository).save(any(Ticket.class));
    }
}