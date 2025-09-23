package com.example.gerenciamento_tickets.service;

import com.example.gerenciamento_tickets.dto.LoginRequestBody;
import com.example.gerenciamento_tickets.dto.UsuarioResponseBody;
import com.example.gerenciamento_tickets.exception.BadRequestException;
import com.example.gerenciamento_tickets.model.Usuario;
import com.example.gerenciamento_tickets.repository.UsuarioRepository;
import com.example.gerenciamento_tickets.security.TokenJWTService;
import com.example.gerenciamento_tickets.util.UsuarioCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AuthService authService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    TokenJWTService tokenJWTService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = UsuarioCreator.usuario();
    }

    @Test
    void register_deveLancarExcecao_quandoUsuarioJaExiste() {
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.of(usuario));
        assertThrows(BadRequestException.class,
                () -> authService.register(UsuarioCreator.registerRequestBody()));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void register_deveRegistrarUsuario_quandoUsuarioNaoExiste() {
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any())).thenReturn(usuario);

        UsuarioResponseBody response = authService.register(UsuarioCreator.registerRequestBody());

        assertNotNull(response);
        assertEquals(usuario.getUsername(), response.username());
        assertEquals(usuario.getRole().name(), response.role());
    }

    @Test
    void login_deveLancarExcecao_quandoCredenciaisInvalidas() {
        LoginRequestBody request = UsuarioCreator.loginRequestBody();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(tokenJWTService);
    }

    @Test
    void login_deveGerarToken_quandoCredenciaisValidas() {
        LoginRequestBody request = UsuarioCreator.loginRequestBody();

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuario);
        when(tokenJWTService.generateToken(usuario)).thenReturn("jwt-token");

        String token = authService.login(request);

        assertEquals("jwt-token", token);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenJWTService).generateToken(usuario);
    }


}