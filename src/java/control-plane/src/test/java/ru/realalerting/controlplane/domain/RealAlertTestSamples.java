package ru.realalerting.controlplane.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class RealAlertTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static RealAlert getRealAlertSample1() {
        return new RealAlert().id(1).name("name1").description("description1").conf("conf1");
    }

    public static RealAlert getRealAlertSample2() {
        return new RealAlert().id(2).name("name2").description("description2").conf("conf2");
    }

    public static RealAlert getRealAlertRandomSampleGenerator() {
        return new RealAlert()
            .id(intCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .conf(UUID.randomUUID().toString());
    }
}
