package com.example.CHdependency.services;

import com.example.CHdependency.configuration.Config;
import com.example.CHdependency.dto.UserPasswordDTO;
import com.example.CHdependency.dto.UserRequestDTO;
import com.example.CHdependency.dto.UserResponseDTO;
import com.example.CHdependency.mappers.UserMapper;
import com.example.CHdependency.repositories.UserRepository;
import com.example.CHdependency.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
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
    public boolean updateUserpassword(UserPasswordDTO userDto){
        if (userDto.getEmail().isEmpty()) return false;
        var user = userRepository.findByEmail(userDto.getEmail());
        if (user == null) return false;
        boolean isPwd = config.password().matches(userDto.getPassword(), user.getPassword());
        if (!isPwd) return false;
        user.setPassword(config.password().encode(userDto.getNewPassword()));
        userRepository.save(user);
        return true;
    }
    public UserResponseDTO createUser(UserRequestDTO user) {
        var data = userMapper.forUserEntity(user);

    public UserResponseDTO createUser(UserRequestDTO userDto) {
        var user = userMapper.forUserEntity(userDto);

        data.setPassword(config.password().encode(data.getPassword()));

        userRepository.save(data);

        userRepository.save(user);

        return userMapper.forResponse(user);
    }
}
