package com.example.gerenciamento_tickets.service;

import com.example.gerenciamento_tickets.dto.LoginRequestBody;
import com.example.gerenciamento_tickets.dto.RegisterRequestBody;
import com.example.gerenciamento_tickets.dto.UsuarioResponseBody;
import com.example.gerenciamento_tickets.exception.BadRequestException;
import com.example.gerenciamento_tickets.mapper.UsuarioMapper;
import com.example.gerenciamento_tickets.model.Usuario;
import com.example.gerenciamento_tickets.repository.UsuarioRepository;
import com.example.gerenciamento_tickets.security.TokenJWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final TokenJWTService tokenJWTService;

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

    public String login(LoginRequestBody dto) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(dto.username(), dto.password());

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        return tokenJWTService.generateToken((Usuario) authentication.getPrincipal());
    }
}
