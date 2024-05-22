package ru.realalerting.controlplane.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MetricTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Metric getMetricSample1() {
        return new Metric().id(1L).name("name1").description("description1");
    }

    public static Metric getMetricSample2() {
        return new Metric().id(2L).name("name2").description("description2");
    }

    public static Metric getMetricRandomSampleGenerator() {
        return new Metric().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
