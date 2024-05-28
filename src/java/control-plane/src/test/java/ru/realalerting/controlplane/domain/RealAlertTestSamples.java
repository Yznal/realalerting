package ru.realalerting.controlplane.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RealAlertTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static RealAlert getRealAlertSample1() {
        return new RealAlert().id(1L).name("name1").description("description1").conf("conf1");
    }

    public static RealAlert getRealAlertSample2() {
        return new RealAlert().id(2L).name("name2").description("description2").conf("conf2");
    }

    public static RealAlert getRealAlertRandomSampleGenerator() {
        return new RealAlert()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .conf(UUID.randomUUID().toString());
    }
}
