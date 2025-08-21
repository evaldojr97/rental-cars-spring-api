package com.challenge.rental_cars_spring_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rental Cars API")
                        .version("1.0.0")
                        .description("API REST para gerenciamento de aluguéis de carros. " +
                                "Esta API permite listar carros disponíveis, processar aluguéis via arquivo RTN " +
                                "e consultar aluguéis realizados.")
                        .contact(new Contact()
                                .name("Challenge Team")
                                .email("challenge@example.com")
                                .url("https://github.com/challenge/rental-cars"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("Servidor local"),
                        new Server()
                                .url("https://api.rentalcars.com")
                                .description("Servidor de produção")
                ));
    }
}
