package org.sopt.kareer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class KareerApplication {

    public static void main(String[] args) {
        SpringApplication.run(KareerApplication.class, args);
    }

}
