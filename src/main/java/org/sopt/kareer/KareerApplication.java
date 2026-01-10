package org.sopt.kareer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@ConfigurationPropertiesScan
@SpringBootApplication
@EnableScheduling
public class KareerApplication {

    public static void main(String[] args) {
        SpringApplication.run(KareerApplication.class, args);
    }

}
