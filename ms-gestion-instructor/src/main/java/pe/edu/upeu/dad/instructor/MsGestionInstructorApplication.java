package pe.edu.upeu.dad.instructor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MsGestionInstructorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsGestionInstructorApplication.class, args);
    }
}
