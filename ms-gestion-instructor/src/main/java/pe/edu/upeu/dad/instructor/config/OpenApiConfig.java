package pe.edu.upeu.dad.instructor.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI instructorOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API - Microservicio de Instructores")
                .description("Gestion de instructores - Desarrollo de Aplicaciones Distribuidas (UPeU)")
                .version("1.0.0"));
    }
}
