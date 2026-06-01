package pe.edu.upeu.dad.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MsIstanaApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsIstanaApiGatewayApplication.class, args);
    }
}
