package com.example.gerenciamento_tickets.dto;

import com.example.gerenciamento_tickets.model.TicketStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TicketResponseBody(Long id, String titulo, TicketStatus status, LocalDateTime criadoEm,
                                 LocalDateTime prazoParaResolucao,
                                 String criadoPor, String usuarioResponsavel,
                                 List<ComentarioResponseBody> comentarios, String categoria) {

}
