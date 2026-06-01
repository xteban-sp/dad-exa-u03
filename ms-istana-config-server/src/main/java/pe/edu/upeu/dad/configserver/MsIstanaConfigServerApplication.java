package pe.edu.upeu.dad.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class MsIstanaConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsIstanaConfigServerApplication.class, args);
    }
}
