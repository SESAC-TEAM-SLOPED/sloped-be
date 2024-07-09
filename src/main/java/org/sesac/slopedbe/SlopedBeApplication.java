package org.sesac.slopedbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SlopedBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SlopedBeApplication.class, args);
    }

}
