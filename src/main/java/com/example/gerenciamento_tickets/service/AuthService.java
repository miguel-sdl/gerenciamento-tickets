package com.example.gerenciamento_tickets.service;

import com.example.gerenciamento_tickets.dto.RegisterRequestBody;
import com.example.gerenciamento_tickets.dto.UsuarioResponseBody;
import com.example.gerenciamento_tickets.exception.BadRequestException;
import com.example.gerenciamento_tickets.mapper.UsuarioMapper;
import com.example.gerenciamento_tickets.model.Usuario;
import com.example.gerenciamento_tickets.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    public UsuarioResponseBody register(RegisterRequestBody dto) {
        usuarioRepository.findByUsername(dto.username()).ifPresent(usuario -> {
            throw new BadRequestException("Usuario ja existe");
        });

        Usuario usuario = Usuario.builder()
                .username(dto.username())
                .password(passwordEncoder.encode(dto.password()))
                .role(dto.role())
                .build();

        return UsuarioMapper.INSTANCE.toUsuarioResponseBody(usuarioRepository.save(usuario));
    }
}
