package com.propertypilot.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductionProfileConfigurationTest {

    @Test
    void productionProfileDisablesPublicApiDocumentation() throws IOException {
        List<PropertySource<?>> sources = new YamlPropertySourceLoader()
                .load("production", new ClassPathResource("application-prod.yml"));
        PropertySource<?> production = sources.get(0);

        assertEquals(false, production.getProperty("springdoc.api-docs.enabled"));
        assertEquals(false, production.getProperty("springdoc.swagger-ui.enabled"));
    }
}
