package com.kharvoll.kharvollbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KharvollBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(KharvollBotApplication.class, args);
    }

}
