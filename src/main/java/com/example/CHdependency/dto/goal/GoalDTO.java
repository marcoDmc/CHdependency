package com.example.CHdependency.dto.goal;

import com.example.CHdependency.enums.goal.Goal;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GoalDTO {
    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private String password;
    @JsonProperty("name")
    private String name;
    @Enumerated(EnumType.STRING)
    private Goal range;
    @JsonProperty("time")
    private int time;
}
