package com.example.CHdependency.services;

import com.example.CHdependency.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

}
