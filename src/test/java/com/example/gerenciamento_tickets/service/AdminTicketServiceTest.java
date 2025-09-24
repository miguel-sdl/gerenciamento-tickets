package com.example.gerenciamento_tickets.service;

import com.example.gerenciamento_tickets.dto.CategoriaResponseBody;
import com.example.gerenciamento_tickets.exception.BadRequestException;
import com.example.gerenciamento_tickets.model.Categoria;
import com.example.gerenciamento_tickets.repository.CategoriaRepository;
import com.example.gerenciamento_tickets.repository.UsuarioRepository;
import com.example.gerenciamento_tickets.util.CategoriaCreator;
import com.example.gerenciamento_tickets.util.UsuarioCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminTicketServiceTest {

    @InjectMocks
    private AdminTicketService adminTicketService;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

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

}