package com.example.gerenciamento_tickets.exception;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@SuperBuilder
public class MethodArgumentNotValidExceptionResponse extends ExceptionResponse {
    private Map<String, String> fieldErrors;

}
