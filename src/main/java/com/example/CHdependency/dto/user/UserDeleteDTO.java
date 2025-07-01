package com.example.CHdependency.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDeleteDTO {
    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private String password;
}
