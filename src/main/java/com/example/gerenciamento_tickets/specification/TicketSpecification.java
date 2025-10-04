package com.example.gerenciamento_tickets.specification;

import com.example.gerenciamento_tickets.model.Ticket;
import com.example.gerenciamento_tickets.model.TicketStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TicketSpecification {

    public static Specification<Ticket> hasUsuarioResponsavel(Long usuarioId) {
        return (root, query, cb) ->
                usuarioId == null ? null : cb.equal(root.get("usuarioResponsavel").get("id"), usuarioId);
    }

    public static Specification<Ticket> hasStatus(TicketStatus status) {
        return ((root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status));
    }

    public static Specification<Ticket> hasCategoria(String categoria) {
        return ((root, query, cb) ->
                categoria == null ? null : cb.equal(root.get("categoria").get("nome"), categoria));
    }

    public static Specification<Ticket> isVencido(Boolean vencido) {
        return (root, query, cb) -> {
            LocalDateTime now = LocalDateTime.now();
            if (vencido == null) return null;
            return vencido ? cb.lessThan(root.get("prazoParaResolucao"), now)
                    : cb.greaterThan(root.get("prazoParaResolucao"), now);
        };
    }

    public static Specification<Ticket> isResolvidoAposPrazo(Boolean resolvidoAposPrazo) {
        return (root, query, cb) -> {
            if (resolvidoAposPrazo == null) return null;
            return resolvidoAposPrazo ? cb.greaterThan(root.get("resolvidoEm"), root.get("prazoParaResolucao"))
                    : cb.lessThan(root.get("resolvidoEm"), root.get("prazoParaResolucao"));
        };
    }

}
