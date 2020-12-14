package eu.venthe.combined;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableConfigurationProperties
@SpringBootApplication
@EnableAsync
public class CombinedApplication {

    public static void main(String[] args) {
        SpringApplication.run(CombinedApplication.class, args);
    }

}
