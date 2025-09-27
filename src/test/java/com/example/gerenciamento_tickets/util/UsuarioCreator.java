package com.example.gerenciamento_tickets.util;

import com.example.gerenciamento_tickets.dto.LoginRequestBody;
import com.example.gerenciamento_tickets.dto.RegisterRequestBody;
import com.example.gerenciamento_tickets.model.UserRole;
import com.example.gerenciamento_tickets.model.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UsuarioCreator {

    private static final String USUARIO_USERNAME = "usuario";
    private static final String TECNICO_USERNAME = "tecnico";
    private static final String ADMIN_USERNAME = "admin";
    private static final String PASSWORD = "password";

    public static Usuario usuario() {
        return Usuario.builder()
                .id(1L)
                .username(USUARIO_USERNAME)
                .role(UserRole.USER)
                .build();
    }

    public static Usuario tecnico() {
        return Usuario.builder()
                .id(2L)
                .username(TECNICO_USERNAME)
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
                .username(ADMIN_USERNAME)
                .role(UserRole.ADMIN)
                .build();
    }

    public static RegisterRequestBody registerRequestBody() {
        return new RegisterRequestBody(USUARIO_USERNAME, PASSWORD, UserRole.USER);
    }

    public static LoginRequestBody loginRequestBody() {
        return new LoginRequestBody(USUARIO_USERNAME, PASSWORD);
    }

    public static Usuario usuarioParaSalvar(PasswordEncoder passwordEncoder) {
        return Usuario.builder()
                .username(USUARIO_USERNAME)
                .password(passwordEncoder.encode(PASSWORD))
                .role(UserRole.USER).build();
    }

    public static Usuario tecnicoParaSalvar(PasswordEncoder passwordEncoder) {
        return Usuario.builder()
                .username(TECNICO_USERNAME)
                .password(passwordEncoder.encode(PASSWORD))
                .role(UserRole.TECNICO).build();
    }

    public static Usuario adminParaSalvar(PasswordEncoder passwordEncoder) {
        return Usuario.builder()
                .username(ADMIN_USERNAME)
                .password(passwordEncoder.encode(PASSWORD))
                .role(UserRole.ADMIN).build();
    }

}
