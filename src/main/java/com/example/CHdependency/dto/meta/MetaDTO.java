package com.example.CHdependency.dto.meta;

import com.example.CHdependency.enums.meta.Meta;
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
public class MetaDTO {
    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private String password;
    @JsonProperty("name")
    private String name;
    @Enumerated(EnumType.STRING)
    private Meta range;
    @JsonProperty("time")
    private int time;
}
