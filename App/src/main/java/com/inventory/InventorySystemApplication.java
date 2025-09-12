package com.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Aplicação principal do Sistema de Inventário Distribuído
 * 
 * Features implementadas:
 * - APIs REST para operações de inventário
 * - Persistência em arquivos JSON
 * - Tolerância a falhas com Circuit Breaker
 * - Controle de concorrência para consistência
 * - Documentação automática com Swagger
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class InventorySystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventorySystemApplication.class, args);
        System.out.println("\n=== Sistema de Inventário Iniciado ===");
        System.out.println("Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println("Health Check: http://localhost:8080/actuator/health");
        System.out.println("====================================\n");
    }
}