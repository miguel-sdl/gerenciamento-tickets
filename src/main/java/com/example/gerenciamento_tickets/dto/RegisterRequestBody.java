package com.example.gerenciamento_tickets.dto;

import com.example.gerenciamento_tickets.model.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestBody(@NotEmpty @NotNull @NotBlank String username,
                                  @NotEmpty @NotNull @NotBlank String password,
                                  @NotNull UserRole role) {
}
