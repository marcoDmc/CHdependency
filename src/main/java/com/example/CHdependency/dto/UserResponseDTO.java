package com.example.CHdependency.dto;

import com.example.CHdependency.enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class UserResponseDTO {
    private String name;
    private String email;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private int age;
}
