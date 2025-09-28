package com.example.gerenciamento_tickets.controller;

import com.example.gerenciamento_tickets.dto.AtualizarCategoriaRequestBody;
import com.example.gerenciamento_tickets.dto.CategoriaResponseBody;
import com.example.gerenciamento_tickets.dto.CriarCategoriaRequestBody;
import com.example.gerenciamento_tickets.service.AdminTicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/tickets")
public class AdminTicketsController {

    private final AdminTicketService adminTicketService;

    @PostMapping("/categoria")
    public ResponseEntity<CategoriaResponseBody> criarCategoria(@RequestBody @Valid CriarCategoriaRequestBody dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminTicketService.criarCategoria(dto));
    }

    @PutMapping("/categoria")
    public ResponseEntity<Void> atualizarCategoria(@RequestBody @Valid AtualizarCategoriaRequestBody dto) {
        adminTicketService.atualizarCategoria(dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
