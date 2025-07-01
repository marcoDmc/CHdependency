package com.example.CHdependency.mappers;

import com.example.CHdependency.dto.UserRequestDTO;
import com.example.CHdependency.dto.UserResponseDTO;
import com.example.CHdependency.entities.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-29T15:06:14-0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.2.jar, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User forUserEntity(UserRequestDTO user) {
        if ( user == null ) {
            return null;
        }

        User user1 = new User();

        user1.setName( user.getName() );
        user1.setAge( user.getAge() );
        user1.setGender( user.getGender() );
        user1.setEmail( user.getEmail() );
        user1.setPassword( user.getPassword() );

        return user1;
    }

    @Override
    public UserResponseDTO forResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDTO userResponseDTO = new UserResponseDTO();

        userResponseDTO.setName( user.getName() );
        userResponseDTO.setEmail( user.getEmail() );
        userResponseDTO.setGender( user.getGender() );
        if ( user.getAge() != null ) {
            userResponseDTO.setAge( user.getAge() );
        }

        return userResponseDTO;
    }
}
