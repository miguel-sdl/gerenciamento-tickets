package com.example.gerenciamento_tickets.dto;

import java.util.List;

public record CategoriaResponseBody(long id, String nome, int prazoDefaultEmHoras,
                                    List<UsuarioResponseBody> usuariosResponsaveis) {
}
