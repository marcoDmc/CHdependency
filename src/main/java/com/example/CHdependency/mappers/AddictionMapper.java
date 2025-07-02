package com.example.CHdependency.mappers;

import com.example.CHdependency.dto.addiction.AddictionRequestDTO;
import com.example.CHdependency.dto.addiction.AddictionResponseDTO;
import com.example.CHdependency.entities.Addiction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel="spring")
public interface AddictionMapper {

    com.example.CHdependency.mappers.UserMapper INSTANCE = Mappers.getMapper(com.example.CHdependency.mappers.UserMapper.class);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Addiction forAddictionEntity(AddictionRequestDTO addiction);

    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    AddictionResponseDTO forResponse(Addiction addiction);

}
