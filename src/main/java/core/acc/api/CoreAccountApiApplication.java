package core.acc.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CoreAccountApiApplication {

    public static void main(String[] args) {
        System.setProperty("spring.main.lazy-initialization", "true");
        SpringApplication.run(CoreAccountApiApplication.class, args);
    }

}
