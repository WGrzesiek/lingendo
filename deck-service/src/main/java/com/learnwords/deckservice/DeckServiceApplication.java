package com.learnwords.deckservice;

import com.learnwords.deckservice.config.DeckNativeHints;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ImportRuntimeHints;

@EnableFeignClients
@ImportRuntimeHints(DeckNativeHints.class)
@SpringBootApplication
public class DeckServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeckServiceApplication.class, args);
    }

}
