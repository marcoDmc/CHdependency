package com.example.CHdependency.services;

import com.example.CHdependency.models.User;
import com.example.CHdependency.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServices {
    final UserRepository userRepository;

    UserServices(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User createUser(User user) {

       return userRepository.save(user);
    }
}
