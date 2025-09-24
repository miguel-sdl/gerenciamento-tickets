package com.example.gerenciamento_tickets.mapper;

import com.example.gerenciamento_tickets.dto.CategoriaResponseBody;
import com.example.gerenciamento_tickets.model.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public abstract class CategoriaMapper extends MapperUtil {
    public static final CategoriaMapper INSTANCE = Mappers.getMapper(CategoriaMapper.class);

    public abstract CategoriaResponseBody toCategoriaResponseBody(Categoria categoria);
}
