package com.example.CHdependency.services;

import com.example.CHdependency.configuration.ConfigAuthentication;
import com.example.CHdependency.dto.goal.DeleteGoalDTO;
import com.example.CHdependency.dto.goal.FindGoalPeriodDTO;
import com.example.CHdependency.dto.goal.GoalDTO;
import com.example.CHdependency.entities.Goal;
import com.example.CHdependency.entities.User;
import com.example.CHdependency.repositories.AddictionRepository;
import com.example.CHdependency.repositories.GoalRepository;
import com.example.CHdependency.repositories.UserRepository;
import com.example.CHdependency.utils.Utils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Period;
import java.util.HashMap;
import java.util.Map;


@Service
public class GoalServices {

    private final GoalRepository metaRepository;
    private final UserRepository userRepository;
    private final AddictionRepository addictionRepository;
    private final Utils utils = new Utils();
    private ConfigAuthentication config;

    GoalServices(GoalRepository metaRepository,
                 UserRepository userRepository,
                 AddictionRepository addictionRepository) {
        this.metaRepository = metaRepository;
        this.userRepository = userRepository;
        this.addictionRepository = addictionRepository;


    }

    @Transactional
    public boolean create(GoalDTO meta) {

        if (!utils.validateName(meta.getName())) return false;
        if (meta.getPassword() == null) return false;
        if (!utils.validateEmail(meta.getEmail())) return false;

        User user = userRepository.findByEmail(meta.getEmail());
        if (user == null) return false;

        boolean isValid = config.password().matches(meta.getPassword(), user.getPassword());
        if (!isValid) return false;

        Goal newMeta = new Goal();

        newMeta.setName(meta.getName());
        Period periodo = utils.returnPeriod(meta.getTime(), meta.getRange());
        newMeta.setPeriod(periodo);
        newMeta.setUser(user);

        metaRepository.save(newMeta);

        return true;
    }

    @Transactional
    public Map<String, Object> findPeriod(FindGoalPeriodDTO period) {
        if (period.getName() == null) return null;
        if (period.getPassword() == null) return null;
        if (!utils.validateEmail(period.getEmail()) || period.getEmail() == null) return null;

        User user = userRepository.findByEmail(period.getEmail());
        if (user == null) return null;

        boolean isValid = config.password().matches(period.getPassword(), user.getPassword());
        if (!isValid) return null;

        Goal meta = metaRepository.findByName(period.getName());
        if (meta == null) return null;

        Period p = Period.parse(meta.getPeriod().toString());
        Map<String, Object> json = new HashMap<>();
        json.put("days", p.getDays());
        json.put("weeks", (p.getMonths() / 4));
        json.put("months", p.getMonths());

        return json;

    }

    @Transactional
    public boolean delete(DeleteGoalDTO meta) {
        User user = userRepository.findByEmail(meta.getEmail());
        if (user == null) return false;

        boolean isValid = config.password().matches(meta.getPassword(), user.getPassword());
        if (!isValid) return false;

        Goal metas = metaRepository.findByName(meta.getName());
        if (metas == null) return false;

        var addiction = addictionRepository.findByUserId(user.getId());
        if (addiction == null) return false;

        metaRepository.delete(metas);
        addictionRepository.delete(addiction);
        return true;
    }
}
