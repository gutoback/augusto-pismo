package com.pismo.augusto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AugustoPismoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AugustoPismoApplication.class, args);
    }

}
