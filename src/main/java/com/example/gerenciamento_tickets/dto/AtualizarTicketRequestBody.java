package com.example.gerenciamento_tickets.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AtualizarTicketRequestBody(@NotNull @Positive Long id, Integer prazoParaAdicionar, Long usuarioResponsavelID) {
}
