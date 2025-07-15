package com.example.CHdependency.configuration;

import com.example.CHdependency.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class DetailsServiceUser implements UserDetailsService {
    private final UserRepository userRepository;


    DetailsServiceUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByName(username)
                .map(UserAuthentication::new)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }
}
