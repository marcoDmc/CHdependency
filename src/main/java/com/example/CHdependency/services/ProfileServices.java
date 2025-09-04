package com.example.CHdependency.services;

import com.example.CHdependency.entities.Profile;
import com.example.CHdependency.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileServices {
    private final ProfileRepository profileRepository;

    ProfileServices(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile findProfile(Long id){
        return profileRepository.findByUserId(id);
    }
}
