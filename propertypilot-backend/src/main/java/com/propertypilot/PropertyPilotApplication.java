package com.propertypilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@EnableScheduling
public class PropertyPilotApplication {

    public static void main(String[] args) {
        SpringApplication.run(PropertyPilotApplication.class, args);
    }
}
