package com.example.CHdependency.services;

import com.example.CHdependency.configuration.Config;
import com.example.CHdependency.dto.meta.DeleteMeta;
import com.example.CHdependency.dto.meta.FindPeriodDTO;
import com.example.CHdependency.dto.meta.MetaDTO;
import com.example.CHdependency.entities.Metas;
import com.example.CHdependency.entities.User;
import com.example.CHdependency.repositories.MetaRepository;
import com.example.CHdependency.repositories.UserRepository;
import com.example.CHdependency.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Period;
import java.util.HashMap;
import java.util.Map;

@Service
public class MetaServices {

    private final MetaRepository metaRepository;
    private final UserRepository userRepository;
    private final AddictionRepository addictionRepository;
    private final Utils utils =  new Utils();

    @Autowired
    private Config config;

    MetaServices(MetaRepository metaRepository,
                 UserRepository userRepository,
                 AddictionRepository addictionRepository){
        this.metaRepository = metaRepository;
        this.userRepository = userRepository;
        this.addictionRepository = addictionRepository;


    }

    public boolean create(MetaDTO meta){
        User user = userRepository.findByEmail(meta.getEmail());
        if (user == null) return false;

        boolean isValid = config.password().matches(meta.getPassword(), user.getPassword());
        if (!isValid) return false;

        Metas newMeta = new Metas();

        newMeta.setName(meta.getName());
        Period periodo = utils.returnPeriod(meta.getTime(), meta.getRange());
        newMeta.setPeriod(periodo);
        newMeta.setUser(user);

        metaRepository.save(newMeta);

        return true;
    }

    public Map<String, Object> findPeriod(FindPeriodDTO period){
        User user = userRepository.findByEmail(period.getEmail());
        if (user == null) return null;

        boolean isValid = config.password().matches(period.getPassword(), user.getPassword());
        if (!isValid) return null;

        Metas meta = metaRepository.findByName(period.getName());
        if (meta == null) return null;

        Period p = Period.parse(meta.getPeriod().toString());
        Map<String, Object> json = new HashMap<>();
        json.put("days", p.getDays());
        json.put("weeks", (p.getMonths() / 4));
        json.put("months", p.getMonths());

        return json;

    }

    public boolean delete(DeleteMeta meta){
        User user = userRepository.findByEmail(meta.getEmail());
        if (user == null) return false;

        boolean isValid = config.password().matches(meta.getPassword(), user.getPassword());
        if (!isValid) return false;

        Metas metas = metaRepository.findByName(meta.getName());
        if (metas == null) return false;

        var addiction = addictionRepository.findByUserId(user.getId());
        if (addiction == null) return false;

        metaRepository.delete(metas);
        addictionRepository.delete(addiction);
        return true;
    }
}
