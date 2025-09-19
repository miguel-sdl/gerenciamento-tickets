package com.example.gerenciamento_tickets.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }

  public UnauthorizedException() {
  }
}
