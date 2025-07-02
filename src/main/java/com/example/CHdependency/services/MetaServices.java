package com.example.CHdependency.services;

import com.example.CHdependency.configuration.Config;
import com.example.CHdependency.dto.meta.MetaDTO;
import com.example.CHdependency.entities.Metas;
import com.example.CHdependency.entities.User;
import com.example.CHdependency.repositories.MetaRepository;
import com.example.CHdependency.repositories.UserRepository;
import com.example.CHdependency.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Period;

@Service
public class MetaServices {

    private final MetaRepository metaRepository;
    private final UserRepository userRepository;
    private final Utils utils =  new Utils();

    @Autowired
    private Config config;

    MetaServices(MetaRepository metaRepository, UserRepository userRepository){
        this.metaRepository = metaRepository;
    }
}
