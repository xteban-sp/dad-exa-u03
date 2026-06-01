package pe.edu.upeu.dad.registryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class MsAdminRegistryServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsAdminRegistryServerApplication.class, args);
    }
}
