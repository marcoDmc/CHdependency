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
}
