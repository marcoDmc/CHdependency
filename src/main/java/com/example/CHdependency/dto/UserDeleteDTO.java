package com.example.CHdependency.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.annotation.WebServlet;
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
