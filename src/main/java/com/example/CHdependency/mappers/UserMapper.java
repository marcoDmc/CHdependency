package com.example.CHdependency.mappers;

import com.example.CHdependency.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.CHdependency.dto.user.UserRequestDTO;
import com.example.CHdependency.dto.user.UserResponseDTO;

@Mapper(componentModel="spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addictions", ignore = true)
    @Mapping(target = "goals", ignore = true)
    User forUserEntity(UserRequestDTO user);
    UserResponseDTO forResponse(User user);
}

