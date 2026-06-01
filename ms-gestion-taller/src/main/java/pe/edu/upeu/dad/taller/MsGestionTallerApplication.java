package pe.edu.upeu.dad.taller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class MsGestionTallerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsGestionTallerApplication.class, args);
    }
}
