package com.example.gerenciamento_tickets.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ComentarioResponseBody(String autor, LocalDateTime criadoEm, String texto) {
}
