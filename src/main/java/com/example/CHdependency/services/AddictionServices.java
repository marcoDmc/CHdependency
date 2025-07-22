package com.example.CHdependency.services;

import com.example.CHdependency.configuration.ConfigAuthentication;
import com.example.CHdependency.dto.addiction.AddictionDTO;
import com.example.CHdependency.dto.addiction.DeleteAddictionDTO;
import com.example.CHdependency.entities.Addiction;
import com.example.CHdependency.entities.User;
import com.example.CHdependency.repositories.AddictionRepository;
import com.example.CHdependency.repositories.GoalRepository;
import com.example.CHdependency.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AddictionServices {
    private final AddictionRepository addictionRepository;
    private final UserRepository userRepository;
    private final GoalRepository metaRepository;
    private ConfigAuthentication config;


    AddictionServices(AddictionRepository addictionRepository,
                      UserRepository userRepository,
                      GoalRepository metaRepository) {
        this.addictionRepository = addictionRepository;
        this.userRepository = userRepository;
        this.metaRepository = metaRepository;
        this.config = config;
        this.utils = utils;
    }

    public boolean create(AddictionDTO addictionDto) {
        if (addictionDto.getType() == null) return false;
        if (addictionDto.getPassword() == null) return false;
        if (!utils.validateEmail(addictionDto.getEmail())) return false;

        User user = userRepository.findByEmail(addictionDto.getEmail());
        if (user == null) return false;

        boolean isValid = config.password().matches(addictionDto.getPassword(), user.getPassword());
        if (!isValid) return false;

        Addiction addiction = new Addiction();
        addiction.setType(addictionDto.getType());
        addiction.setUser(user);
        addictionRepository.save(addiction);

        return true;
    }

    public boolean delete(DeleteAddictionDTO addiction) {
        if (addiction.getType() == null) return false;
        if (addiction.getPassword() == null) return false;
        if (!utils.validateEmail(addiction.getEmail())) return false;

        User user = userRepository.findByEmail(addiction.getEmail());
        if (user == null) return false;

        boolean isValid = config.password().matches(addiction.getPassword(), user.getPassword());
        if (!isValid) return false;

        Addiction addictions = addictionRepository.findByUserId(user.getId());
        if (addictions == null) return false;

        var meta = metaRepository.findByUserId(user.getId());
        if (meta == null) return false;

        metaRepository.delete(meta);
        addictionRepository.delete(addictions);
        return true;
    }
}
