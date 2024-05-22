package ru.realalerting.controlplane.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MetricSubscriberTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MetricSubscriber getMetricSubscriberSample1() {
        return new MetricSubscriber()
            .id(1L)
            .subscriberAddress("subscriberAddress1")
            .subscriberPort(1)
            .subscriberUri("subscriberUri1")
            .subscriberStreamId(1);
    }

    public static MetricSubscriber getMetricSubscriberSample2() {
        return new MetricSubscriber()
            .id(2L)
            .subscriberAddress("subscriberAddress2")
            .subscriberPort(2)
            .subscriberUri("subscriberUri2")
            .subscriberStreamId(2);
    }

    public static MetricSubscriber getMetricSubscriberRandomSampleGenerator() {
        return new MetricSubscriber()
            .id(longCount.incrementAndGet())
            .subscriberAddress(UUID.randomUUID().toString())
            .subscriberPort(intCount.incrementAndGet())
            .subscriberUri(UUID.randomUUID().toString())
            .subscriberStreamId(intCount.incrementAndGet());
    }
}
