package com.example.CHdependency.services;

import com.example.CHdependency.configuration.ConfigAuthentication;
import com.example.CHdependency.dto.user.UserDeleteDTO;
import com.example.CHdependency.dto.user.UserPasswordDTO;
import com.example.CHdependency.dto.user.UserRequestDTO;
import com.example.CHdependency.dto.user.UserResponseDTO;
import com.example.CHdependency.entities.User;
import com.example.CHdependency.mappers.UserMapper;
import com.example.CHdependency.repositories.RefreshTokenRepository;
import com.example.CHdependency.repositories.UserRepository;
import com.example.CHdependency.utils.Utils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServices {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Utils utils;
    private final ConfigAuthentication config;
    private final RefreshTokenRepository refreshTokenRepository;


    public UserServices(UserRepository userRepository,
                        UserMapper userMapper,
                        Utils utils,
                        ConfigAuthentication config,
                        RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.utils = utils;
        this.config = config;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public boolean updatePassword(UserPasswordDTO userDto) {
        if (userDto.getEmail().isEmpty()) return false;
        var user = userRepository.findByEmail(userDto.getEmail());
        if (user == null) return false;
        boolean isPwd = config.password().matches(userDto.getPassword(), user.getPassword());
        if (!isPwd) return false;
        boolean verifyPasswordIsEqual = config.password().matches(userDto.getNewPassword(), user.getPassword());
        if (verifyPasswordIsEqual) return false;
        user.setPassword(config.password().encode(userDto.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    public boolean delete(UserDeleteDTO userDto) {
        var user = userRepository.findByEmail(userDto.getEmail());
        if (user == null) return false;

        boolean isValid = config.password().matches(userDto.getPassword(), user.getPassword());
        if (!isValid) return false;

        userRepository.delete(user);
        return true;
    }


    public UserResponseDTO create(UserRequestDTO userDto) {
        var user = userMapper.forUserEntity(userDto);

        if (!utils.validateEmail(user.getEmail())) return null;
        if (!utils.validatePassword(user.getPassword())) return null;
        if (!utils.validateName(user.getName())) return null;

        user.setPassword(config.password().encode(user.getPassword()));

        userRepository.save(user);

        return userMapper.forResponse(user);
    }
}
