package ru.realalerting.controlplane.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MetricTagsValueTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MetricTagsValue getMetricTagsValueSample1() {
        return new MetricTagsValue().id(1L).value01("value011").value256("value2561");
    }

    public static MetricTagsValue getMetricTagsValueSample2() {
        return new MetricTagsValue().id(2L).value01("value012").value256("value2562");
    }

    public static MetricTagsValue getMetricTagsValueRandomSampleGenerator() {
        return new MetricTagsValue()
            .id(longCount.incrementAndGet())
            .value01(UUID.randomUUID().toString())
            .value256(UUID.randomUUID().toString());
    }
}
