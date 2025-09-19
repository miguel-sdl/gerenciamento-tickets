package com.example.gerenciamento_tickets.exception;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public class ExceptionResponse {

    private int status;
    private String title;
    private String message;
    private LocalDateTime timestamp;
}
