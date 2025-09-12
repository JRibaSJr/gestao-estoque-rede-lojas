package com.inventory.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger/OpenAPI para documentação automática das APIs
 */
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Inventário Distribuído")
                        .description("APIs REST para gerenciamento de estoque com tolerância a falhas")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("JRibaSJr")
                                .email("junior.rib@gmail.com")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Servidor de Desenvolvimento"));
    }
}