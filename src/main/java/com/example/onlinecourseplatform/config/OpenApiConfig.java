package com.example.onlinecourseplatform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI documentation.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Custom OpenAPI bean to define API metadata.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Online Course Platform API")
                        .version("1.0")
                        .description("REST API documentation for the Online Course Platform project")
                        .contact(new Contact().name("Saikrishna").email("your.email@example.com"))
                );
    }
}

