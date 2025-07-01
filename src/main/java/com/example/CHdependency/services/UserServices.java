package com.example.CHdependency.services;

import com.example.CHdependency.configuration.Config;
import com.example.CHdependency.dto.UserRequestDTO;
import com.example.CHdependency.dto.UserResponseDTO;
import com.example.CHdependency.mappers.UserMapper;
import com.example.CHdependency.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServices {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Utils utils =  new Utils();

    @Autowired
    private Config config;



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
