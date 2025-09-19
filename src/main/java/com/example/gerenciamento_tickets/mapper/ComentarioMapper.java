package com.example.gerenciamento_tickets.mapper;

import com.example.gerenciamento_tickets.dto.ComentarioResponseBody;
import com.example.gerenciamento_tickets.model.Comentario;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public abstract class ComentarioMapper extends MapperUtil {

    public static final ComentarioMapper INSTANCE = Mappers.getMapper(ComentarioMapper.class);

    public abstract ComentarioResponseBody toComentarioResponseBody(Comentario comentario);
}
