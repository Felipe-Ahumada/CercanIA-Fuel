package cl.bencinaenlinea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BencinaEnLineaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BencinaEnLineaApplication.class, args);
    }
}
