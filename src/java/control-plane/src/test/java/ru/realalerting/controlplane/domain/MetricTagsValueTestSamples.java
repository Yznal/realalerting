package ru.realalerting.controlplane.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MetricTagsValueTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MetricTagsValue getMetricTagsValueSample1() {
        return new MetricTagsValue().id(1L).value1("value11").value256("value2561");
    }

    public static MetricTagsValue getMetricTagsValueSample2() {
        return new MetricTagsValue().id(2L).value1("value12").value256("value2562");
    }

    public static MetricTagsValue getMetricTagsValueRandomSampleGenerator() {
        return new MetricTagsValue()
            .id(longCount.incrementAndGet())
            .value1(UUID.randomUUID().toString())
            .value256(UUID.randomUUID().toString());
    }
}
