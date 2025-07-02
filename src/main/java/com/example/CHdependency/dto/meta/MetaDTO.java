package com.example.CHdependency.dto.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MetaDTO {
    @JsonProperty("name")
    private String name;
    @Enumerated(EnumType.STRING)
    private Meta range;
    @JsonProperty("time")
    private String time;
}
