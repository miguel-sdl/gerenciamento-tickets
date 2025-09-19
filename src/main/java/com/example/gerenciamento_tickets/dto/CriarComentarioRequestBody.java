package com.example.gerenciamento_tickets.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CriarComentarioRequestBody(@NotNull long ticketId, @NotNull @NotEmpty String texto) {

}
