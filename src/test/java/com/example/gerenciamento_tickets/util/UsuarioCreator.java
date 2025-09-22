package com.example.gerenciamento_tickets.util;

import com.example.gerenciamento_tickets.dto.RegisterRequestBody;
import com.example.gerenciamento_tickets.model.UserRole;
import com.example.gerenciamento_tickets.model.Usuario;

public class UsuarioCreator {

    public static Usuario usuario() {
        return Usuario.builder()
                .id(1L)
                .username("usuario")
                .role(UserRole.USER)
                .build();
    }

    public static Usuario tecnico() {
        return Usuario.builder()
                .id(2L)
                .username("tecnico")
                .role(UserRole.TECNICO)
                .build();
    }

    public static Usuario outroUsuario() {
        return Usuario.builder()
                .id(3L)
                .username("outroUsuario")
                .role(UserRole.USER)
                .build();
    }

    public static Usuario outroTecnico() {
        return Usuario.builder()
                .id(4L)
                .username("outroTecnico")
                .role(UserRole.TECNICO)
                .build();
    }

    public static Usuario admin() {
        return Usuario.builder()
                .id(5L)
                .username("admin")
                .role(UserRole.ADMIN)
                .build();
    }

    public static RegisterRequestBody registerRequestBody() {
        return new RegisterRequestBody("usuario", "password", UserRole.USER);
    }
}
