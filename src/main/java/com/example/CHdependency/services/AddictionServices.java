package com.example.CHdependency.services;

import com.example.CHdependency.configuration.Config;
import com.example.CHdependency.dto.addiction.AddictionDTO;
import com.example.CHdependency.entities.Addiction;
import com.example.CHdependency.entities.User;
import com.example.CHdependency.repositories.AddictionRepository;
import com.example.CHdependency.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddictionServices {
    final AddictionRepository addictionRepository;
    AddictionServices(AddictionRepository addictionRepository){
        this.addictionRepository = addictionRepository;
    }

    public boolean create(AddictionDTO addictionDto) {
        User user = userRepository.findByEmail(addictionDto.getEmail());
        if(user == null) return false;

        boolean isValid = config.password().matches(addictionDto.getPassword(), user.getPassword());
        if(!isValid) return false;

        Addiction addiction = new Addiction();
        addiction.setType(addictionDto.getType());
        addiction.setUser(user);
        addictionRepository.save(addiction);

        return true;
    }
}
