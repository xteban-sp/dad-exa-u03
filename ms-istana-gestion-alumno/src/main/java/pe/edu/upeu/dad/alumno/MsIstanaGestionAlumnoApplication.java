package pe.edu.upeu.dad.alumno;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MsIstanaGestionAlumnoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsIstanaGestionAlumnoApplication.class, args);
    }
}
