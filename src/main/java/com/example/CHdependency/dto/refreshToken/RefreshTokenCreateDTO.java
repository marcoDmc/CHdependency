package com.example.CHdependency.dto.refreshToken;


import com.example.CHdependency.entities.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenCreateDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("user")
    private User user;
}
