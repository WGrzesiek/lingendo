package com.learnwords.deckservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class DeckServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeckServiceApplication.class, args);
    }

}
