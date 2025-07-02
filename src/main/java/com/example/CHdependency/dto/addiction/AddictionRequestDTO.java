package com.example.CHdependency.dto.addiction;


import com.example.CHdependency.enums.addiction.Addictions;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddictionRequestDTO {
    @JsonProperty("type")
    private Addictions type;
}