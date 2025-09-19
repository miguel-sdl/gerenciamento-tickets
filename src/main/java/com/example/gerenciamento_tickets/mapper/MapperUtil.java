package com.example.gerenciamento_tickets.mapper;

import com.example.gerenciamento_tickets.model.Categoria;
import com.example.gerenciamento_tickets.model.Usuario;

public class MapperUtil {
    protected String map(Usuario value) {
        return value.getUsername();
    }

    protected String map(Categoria value) {
        return value.getNome();
    }
}
