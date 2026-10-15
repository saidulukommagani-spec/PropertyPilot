package com.propertypilot.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class BusinessClockConfig {

    @Bean
    public Clock businessClock() {
        return Clock.systemDefaultZone();
    }
}
