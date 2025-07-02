package com.example.CHdependency.utils;

import com.example.CHdependency.enums.meta.Meta;

import java.time.Period;

public class Utils {
    public boolean validatePassword(String password){
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^+=])[A-Za-z\\d@$!%*?&#^+=]{8,}$";
        return password.matches(passwordRegex);
    }

    public boolean validateEmail(String email){
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    public boolean validateName(String name){
        String nameRegex = "^[A-Za-zÀ-ÖØ-öø-ÿ]+(?: [A-Za-zÀ-ÖØ-öø-ÿ]+)*$";
        return name.matches(nameRegex);
    }
    public Period returnPeriod(int time, Meta meta) {
        switch (meta) {
            case MONTHS:
                return Period.ofMonths(time);
            case DAYS:
                return Period.ofDays(time);
            case WEEKS:
                return Period.ofWeeks(time);
            default:
                throw new IllegalArgumentException("Tipo de meta inválido: " + meta);
        }
    }

}
