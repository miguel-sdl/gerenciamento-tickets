package com.example.gerenciamento_tickets.dto;

import com.example.gerenciamento_tickets.model.TicketStatus;

public record TicketFilter(Long usuarioId, String categoria, TicketStatus status, Boolean vencido) {
}
