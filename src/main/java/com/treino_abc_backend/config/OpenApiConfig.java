package com.treino_abc_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI treinoABCOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Treino API")
                        .description("Documentação da API para gerenciamento de treinos personalizados")
                        .version("v1.0"));
    }
}

