package ru.realalerting.controlplane.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class MetricSubscriberTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MetricSubscriber getMetricSubscriberSample1() {
        return new MetricSubscriber()
            .id(1)
            .subscriberAddress("subscriberAddress1")
            .subscriberPort(1)
            .subscriberUri("subscriberUri1")
            .subscriberStreamId(1);
    }

    public static MetricSubscriber getMetricSubscriberSample2() {
        return new MetricSubscriber()
            .id(2)
            .subscriberAddress("subscriberAddress2")
            .subscriberPort(2)
            .subscriberUri("subscriberUri2")
            .subscriberStreamId(2);
    }

    public static MetricSubscriber getMetricSubscriberRandomSampleGenerator() {
        return new MetricSubscriber()
            .id(intCount.incrementAndGet())
            .subscriberAddress(UUID.randomUUID().toString())
            .subscriberPort(intCount.incrementAndGet())
            .subscriberUri(UUID.randomUUID().toString())
            .subscriberStreamId(intCount.incrementAndGet());
    }
}
