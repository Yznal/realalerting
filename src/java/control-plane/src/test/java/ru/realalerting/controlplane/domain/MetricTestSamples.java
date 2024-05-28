package ru.realalerting.controlplane.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MetricTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Metric getMetricSample1() {
        return new Metric()
            .id(1L)
            .name("name1")
            .description("description1")
            .criticalAlertProducerAddress("criticalAlertProducerAddress1")
            .criticalAlertProducerPort(1)
            .criticalAlertProducerUri("criticalAlertProducerUri1")
            .criticalAlertProducerStreamId(1);
    }

    public static Metric getMetricSample2() {
        return new Metric()
            .id(2L)
            .name("name2")
            .description("description2")
            .criticalAlertProducerAddress("criticalAlertProducerAddress2")
            .criticalAlertProducerPort(2)
            .criticalAlertProducerUri("criticalAlertProducerUri2")
            .criticalAlertProducerStreamId(2);
    }

    public static Metric getMetricRandomSampleGenerator() {
        return new Metric()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .criticalAlertProducerAddress(UUID.randomUUID().toString())
            .criticalAlertProducerPort(intCount.incrementAndGet())
            .criticalAlertProducerUri(UUID.randomUUID().toString())
            .criticalAlertProducerStreamId(intCount.incrementAndGet());
    }
}
