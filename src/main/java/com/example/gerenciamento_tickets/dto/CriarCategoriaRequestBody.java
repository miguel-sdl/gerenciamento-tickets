package com.example.gerenciamento_tickets.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CriarCategoriaRequestBody(@NotNull @NotEmpty @NotBlank String nome,
                                        @NotNull @Positive Integer prazoDefaultEmHoras,
                                        @NotNull @NotEmpty List<Long> usuariosResponsaveisIds) {
}
