package com.example.gerenciamento_tickets.controller;

import com.example.gerenciamento_tickets.dto.CriarComentarioRequestBody;
import com.example.gerenciamento_tickets.dto.CriarTicketRequestBody;
import com.example.gerenciamento_tickets.dto.TicketResponseBody;
import com.example.gerenciamento_tickets.model.Usuario;
import com.example.gerenciamento_tickets.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService ticketService;

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseBody> findById(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ticketService.findById(id, Usuario.builder().build()));
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseBody>> findAllTicketsByUser() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ticketService.findAllTicketsByUser(Usuario.builder().build()));
    }


    @PostMapping
    public ResponseEntity<TicketResponseBody> criarTicket(@RequestBody @Valid CriarTicketRequestBody dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketService.criarTicket(dto, Usuario.builder().build()));
    }

    @PostMapping("/comentario")
    public ResponseEntity<TicketResponseBody> adicionarComentario(@RequestBody @Valid CriarComentarioRequestBody dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketService.adicionarComentario(dto, Usuario.builder().build()));
    }
}
