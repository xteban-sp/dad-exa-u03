package pe.edu.upeu.dad.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MsIstanaAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsIstanaAuthApplication.class, args);
    }
}
