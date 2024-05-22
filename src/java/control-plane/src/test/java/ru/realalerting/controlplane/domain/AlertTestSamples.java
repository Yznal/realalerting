package ru.realalerting.controlplane.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AlertTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Alert getAlertSample1() {
        return new Alert().id(1L).name("name1").description("description1");
    }

    public static Alert getAlertSample2() {
        return new Alert().id(2L).name("name2").description("description2");
    }

    public static Alert getAlertRandomSampleGenerator() {
        return new Alert().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
