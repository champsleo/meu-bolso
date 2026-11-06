package com.meubolso.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

/**
 * Ponto de entrada do back-end. Rode esta classe para subir a API em http://localhost:8080
 * (a autenticação é feita por JWT, por isso o usuário/senha padrão do Spring Security é desligado).
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class MeuBolsoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeuBolsoApplication.class, args);
    }
}
