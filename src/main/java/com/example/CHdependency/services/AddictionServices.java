package com.example.CHdependency.services;

import com.example.CHdependency.repositories.AddictionRepository;
import org.springframework.stereotype.Service;

@Service
public class AddictionServices {
    final AddictionRepository addictionRepository;
    AddictionServices(AddictionRepository addictionRepository){
        this.addictionRepository = addictionRepository;
    }
}
