package ru.realalerting.controlplane.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class TenantTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Tenant getTenantSample1() {
        return new Tenant().id(1).name("name1").description("description1");
    }

    public static Tenant getTenantSample2() {
        return new Tenant().id(2).name("name2").description("description2");
    }

    public static Tenant getTenantRandomSampleGenerator() {
        return new Tenant().id(intCount.incrementAndGet()).name(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
