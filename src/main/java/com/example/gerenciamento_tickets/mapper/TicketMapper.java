package com.example.gerenciamento_tickets.mapper;

import com.example.gerenciamento_tickets.dto.TicketResponseBody;
import com.example.gerenciamento_tickets.model.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public abstract class TicketMapper extends MapperUtil {

    public static final TicketMapper INSTANCE = Mappers.getMapper(TicketMapper.class);

    public abstract TicketResponseBody toTicketResponseBody(Ticket ticket);
}
