package com.example.gerenciamento_tickets.security;

import com.example.gerenciamento_tickets.exception.ExceptionResponse;
import com.example.gerenciamento_tickets.exception.UnauthorizedException;
import com.example.gerenciamento_tickets.model.Usuario;
import com.example.gerenciamento_tickets.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Component
public class JWTSecurityFilter extends OncePerRequestFilter {

    private final TokenJWTService tokenJWTService;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (eNecessarioAutenticar(request)) {
                String token = getToken(request);

                String username = tokenJWTService.validateToken(token);

                Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow(UnauthorizedException::new);

                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
            }

        } catch (Exception e) {
            log.warn("{} (\"{}\") durante a captura e validacao do token JWT", e.getClass().getSimpleName(), e.getMessage());


            response.getWriter().write(ExceptionResponse.builder()
                    .status(HttpServletResponse.SC_UNAUTHORIZED)
                    .title("Unauthorized")
                    .message(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build().toString());

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        filterChain.doFilter(request, response);

    }

    private String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) throw new UnauthorizedException("Token JWT nao encaminhado no header authorization");

        return authHeader.replace("Bearer ", "");
    }

    private boolean eNecessarioAutenticar(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        if (requestURI.contains("swagger") || requestURI.contains("api-docs")) return false;
        return !Arrays.asList(SecurityConfig.ENDPOINTS_PERMITIDOS_SEM_AUTENTICAR).contains(requestURI);
    }

}
