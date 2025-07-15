package com.example.CHdependency.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigSwagger {
    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI().info(new Info()
                        .title("CHdependency")
                        .version("1.0.0")
                        .description("welcome the documentation of CHdependency API.")
                );
    }
}
