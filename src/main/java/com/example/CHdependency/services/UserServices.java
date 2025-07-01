package com.example.CHdependency.services;

import com.example.CHdependency.models.User;
import com.example.CHdependency.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServices {
    final UserRepository userRepository;



    UserServices(UserRepository userRepository,
                 UserMapper userMapper,
                 Config config
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.config = config;
    }

    public UserResponseDTO createUser(UserRequestDTO user) {
        var data = userMapper.forUserEntity(user);

        if (!utils.validateEmail(data.getEmail())) return null;
        if (!utils.validatePassword(data.getPassword())) return null;
        if (!utils.validateName(data.getName())) return null;

        data.setPassword(config.password().encode(data.getPassword()));

        userRepository.save(data);

        return userMapper.forResponse(data);
    }
}
