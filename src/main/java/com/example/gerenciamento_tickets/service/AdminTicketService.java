package com.example.gerenciamento_tickets.service;

import com.example.gerenciamento_tickets.dto.AtualizarCategoriaRequestBody;
import com.example.gerenciamento_tickets.dto.CategoriaResponseBody;
import com.example.gerenciamento_tickets.dto.CriarCategoriaRequestBody;
import com.example.gerenciamento_tickets.exception.BadRequestException;
import com.example.gerenciamento_tickets.exception.NotFoundException;
import com.example.gerenciamento_tickets.mapper.CategoriaMapper;
import com.example.gerenciamento_tickets.model.Categoria;
import com.example.gerenciamento_tickets.model.UserRole;
import com.example.gerenciamento_tickets.model.Usuario;
import com.example.gerenciamento_tickets.repository.CategoriaRepository;
import com.example.gerenciamento_tickets.repository.TicketRepository;
import com.example.gerenciamento_tickets.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
