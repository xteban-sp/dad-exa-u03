package pe.edu.upeu.dad.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI authOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API - Microservicio de Autenticacion")
                .description("Autenticacion y emision de JWT - Desarrollo de Aplicaciones Distribuidas (UPeU)")
                .version("1.0.0"));
    }
}
