package com.example.CHdependency.services;

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
