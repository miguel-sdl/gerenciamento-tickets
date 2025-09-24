package com.example.gerenciamento_tickets.util;

import com.example.gerenciamento_tickets.dto.CriarCategoriaRequestBody;
import com.example.gerenciamento_tickets.model.Categoria;

import java.util.List;

public class CategoriaCreator {

    public static Categoria validCategoria() {
        return Categoria.builder()
                .nome("Suporte")
                .prazoDefaultEmHoras(24)
                .usuariosResponsaveis(List.of(UsuarioCreator.tecnico())).build();
    }
    public static Categoria categoriaSemTecnicoDisponivel() {
        return Categoria.builder()
                .nome("Suporte")
                .prazoDefaultEmHoras(24)
                .usuariosResponsaveis(List.of()).build();

    }

    public static CriarCategoriaRequestBody criarCategoriaRequestBody() {
        return new CriarCategoriaRequestBody("Suporte", 24, List.of(1L, 2L));
    }
}
