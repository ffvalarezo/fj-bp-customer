package com.pichincha.customer.configuration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Basic test for Resilience4j configuration
 * Verifies that our configuration can be instantiated
 */
class ResilienceConfigTest {

    @Test
    void resilienceConfigCanBeInstantiated() {
        // Test that our configuration class can be instantiated
        ResilienceConfig config = new ResilienceConfig();
        assertNotNull(config, "ResilienceConfig should be instantiable");
    }

    @Test
    void resilienceConfigHasCorrectAnnotations() {
        // Test that our configuration class has the @Configuration annotation
        boolean hasConfigurationAnnotation = ResilienceConfig.class
            .isAnnotationPresent(org.springframework.context.annotation.Configuration.class);
        assertTrue(hasConfigurationAnnotation, "ResilienceConfig should have @Configuration annotation");
    }
}
