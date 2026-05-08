package com.laporeon.splog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Splog")
                                .description("""
                                        A REST API to manage posts for your personal blog.
                                        
                                        ## Key Features
                                        - Input validation for post request payloads.
                                        - Full CRUD operations for posts.
                                        - Pagination support with flexible sorting.
                                        - Swagger documentation for all endpoints.
                                        - One-command deployment with Docker Compose
                                        """
                                )
                                .version("1.0.0")
                                .license(new License()
                                        .name("MIT License")
                                        .url("https://opensource.org/licenses/MIT"))
                );
    }
}

