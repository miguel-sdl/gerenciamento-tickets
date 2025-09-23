package com.example.gerenciamento_tickets.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record LoginRequestBody(@NotNull @NotBlank @NotEmpty String username,
                               @NotNull @NotBlank @NotEmpty String password) {
}
