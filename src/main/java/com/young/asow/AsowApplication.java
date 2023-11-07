package com.young.asow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AsowApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsowApplication.class, args);
    }

}
