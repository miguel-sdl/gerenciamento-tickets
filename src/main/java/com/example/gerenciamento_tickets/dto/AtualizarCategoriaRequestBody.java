package com.example.gerenciamento_tickets.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record AtualizarCategoriaRequestBody(@NotNull @Positive Long id, String nome, Integer prazoDefaultEmHoras,
                                            List<Long> usuariosResponsaveis) {
}
