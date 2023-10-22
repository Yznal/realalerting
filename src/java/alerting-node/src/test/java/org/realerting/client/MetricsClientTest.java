package org.realerting.client;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class MetricsClientTest {

    @Inject
    MetricsClient metricsClient;

    @Test
    void calculateAlert() {
        assertFalse(metricsClient.calculateAlert(1, 99.99));
        assertTrue(metricsClient.calculateAlert(1, 100.01));
    }
}