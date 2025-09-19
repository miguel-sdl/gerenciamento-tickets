package com.example.gerenciamento_tickets.util;

import com.example.gerenciamento_tickets.model.UserRole;
import com.example.gerenciamento_tickets.model.Usuario;

public class UsuarioCreator {

    public static Usuario usuario(){
        return Usuario.builder()
                .id(1L)
                .username("usuario")
                .role(UserRole.USER)
                .build();
    }

    public static Usuario tecnico(){
        return Usuario.builder()
                .id(2L)
                .username("tecnico")
                .role(UserRole.TECNICO)
                .build();
    }
}
