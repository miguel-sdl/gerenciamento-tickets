package com.example.gerenciamento_tickets.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CriarTicketRequestBody(@NotEmpty @NotNull String titulo,
                                     @NotEmpty @NotNull String descricao,
                                     @NotEmpty @NotNull String categoria) {
}
