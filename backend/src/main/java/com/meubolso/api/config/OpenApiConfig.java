package com.meubolso.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configura a documentação interativa em http://localhost:8080/swagger-ui.html
 * O botão "Authorize" aceita o token retornado pelo login.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Meu Bolso API",
                version = "1.0",
                description = "API REST do aplicativo de controle financeiro pessoal Meu Bolso"),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT")
public class OpenApiConfig {
}
