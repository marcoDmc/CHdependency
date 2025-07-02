package com.example.CHdependency.services;

import com.example.CHdependency.configuration.Config;
import com.example.CHdependency.dto.meta.MetaDTO;
import com.example.CHdependency.entities.Metas;
import com.example.CHdependency.entities.User;
import com.example.CHdependency.repositories.MetaRepository;
import org.springframework.stereotype.Service;

@Service
public class MetaServices {

    final MetaRepository metaRepository;
    MetaServices(MetaRepository metaRepository){
        this.metaRepository = metaRepository;
    }
}
