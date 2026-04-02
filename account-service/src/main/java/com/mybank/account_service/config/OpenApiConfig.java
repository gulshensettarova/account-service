package com.mybank.account_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Account Service API")
                        .description("Bank hesab idarəetmə sistemi")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("MyBank")
                                .email("gulshensettarova@gmail.com"))
                        .license(new License().name("Private").url("https://abb-bank.az/")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Local"),
                        new Server().url("https://api.mybank.az").description("Production")));
    }
}