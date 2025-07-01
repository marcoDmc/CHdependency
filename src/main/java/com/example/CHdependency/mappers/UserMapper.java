package com.example.CHdependency.mappers;

import com.example.CHdependency.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.CHdependency.dto.UserRequestDTO;
import com.example.CHdependency.dto.UserResponseDTO;

@Mapper(componentModel="spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target="id", ignore=true)
    User forUserEntity(UserRequestDTO user);
    UserResponseDTO forResponse(User user);
}

