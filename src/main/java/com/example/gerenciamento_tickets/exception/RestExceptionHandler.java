package com.example.gerenciamento_tickets.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException e) {
        ExceptionResponse body = ExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .title("Bad Request")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now()).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MethodArgumentNotValidExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> fieldErrors = new HashMap<>();

        e.getFieldErrors().forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        MethodArgumentNotValidExceptionResponse body = MethodArgumentNotValidExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .title("Bad Request")
                .message("Validation fields failed")
                .timestamp(LocalDateTime.now())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body);

    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Void> handleUnauthorizedException(UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentialsException(BadCredentialsException e) {
        ExceptionResponse body = ExceptionResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .title("Bad Credentials")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now()).build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException e) {
        ExceptionResponse body = ExceptionResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .title("Not Found")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now()).build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body);
    }
}
