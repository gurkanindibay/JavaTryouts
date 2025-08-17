package com.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI collectionsSpringOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Collections Spring API")
                        .description("A Spring Boot application for managing a library collection with observability features")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Collections Spring Team")
                                .email("support@collections-spring.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://collections-spring.example.com")
                                .description("Production Server")
                ));
    }
}