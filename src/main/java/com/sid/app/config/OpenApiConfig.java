package com.sid.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for Swagger documentation.
 * Provides API metadata like title, version, contact info, and license.
 * This helps generate a user-friendly Swagger UI or Redoc documentation.
 * <p>
 * Accessible at: http://localhost:8069/swagger-ui.html
 *
 * @author Siddhant
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI specification used for Swagger documentation.
     *
     * @return OpenAPI definition with metadata.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tracker Manager API")
                        .version("1.0.0")
                        .description("This API provides endpoints for managing vehicle tracking, petrol expenses, and more.")
                        .contact(new Contact()
                                .name("Siddhant Patni")
                                .url("https://github.com/siddhantpatni0407")
                                .email("siddhant4patni@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .externalDocs(new ExternalDocumentation()
                        .description("Tracker Manager GitHub Repository")
                        .url("https://github.com/siddhantpatni0407/spring-boot-tracker-manager-service")); // Replace with your actual repo
    }

}