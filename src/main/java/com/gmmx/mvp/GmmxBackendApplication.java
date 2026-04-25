package com.gmmx.mvp;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class GmmxBackendApplication {

    public static void main(String[] args) {
        // Load .env into System Properties
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
        
        dotenv.entries().forEach(entry -> {
            if (entry.getKey() != null && entry.getValue() != null) {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        });

        SpringApplication.run(GmmxBackendApplication.class, args);
    }
}
