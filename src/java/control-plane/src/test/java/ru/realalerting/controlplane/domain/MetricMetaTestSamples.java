package ru.realalerting.controlplane.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MetricMetaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MetricMeta getMetricMetaSample1() {
        return new MetricMeta().id(1L).label1("label11").label256("label2561");
    }

    public static MetricMeta getMetricMetaSample2() {
        return new MetricMeta().id(2L).label1("label12").label256("label2562");
    }

    public static MetricMeta getMetricMetaRandomSampleGenerator() {
        return new MetricMeta().id(longCount.incrementAndGet()).label1(UUID.randomUUID().toString()).label256(UUID.randomUUID().toString());
    }
}
