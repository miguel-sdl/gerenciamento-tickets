package com.example.gerenciamento_tickets.controller;

import com.example.gerenciamento_tickets.dto.*;
import com.example.gerenciamento_tickets.model.TicketStatus;
import com.example.gerenciamento_tickets.service.AdminTicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/tickets")
public class AdminTicketsController {

    private final AdminTicketService adminTicketService;

    @GetMapping
    public ResponseEntity<Page<TicketResponseBody>> filtrarTickets(@RequestParam(required = false) Long usuarioId,
                                                                   @RequestParam(required = false) TicketStatus status,
                                                                   @RequestParam(required = false) String categoria,
                                                                   @RequestParam(required = false) Boolean vencido,
                                                                   Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(adminTicketService.filterTickets(new TicketFilter(usuarioId, categoria, status, vencido), pageable));
    }


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

    @PutMapping
    public ResponseEntity<Void> atualizarTicket(@RequestBody @Valid AtualizarTicketRequestBody dto) {
        adminTicketService.atualzarTicket(dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
