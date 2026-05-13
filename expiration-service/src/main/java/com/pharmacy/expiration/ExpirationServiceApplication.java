package com.pharmacy.expiration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExpirationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpirationServiceApplication.class, args);
    }
}
