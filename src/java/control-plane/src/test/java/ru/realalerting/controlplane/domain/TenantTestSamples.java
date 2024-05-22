package ru.realalerting.controlplane.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TenantTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Tenant getTenantSample1() {
        return new Tenant().id(1L).name("name1").description("description1");
    }

    public static Tenant getTenantSample2() {
        return new Tenant().id(2L).name("name2").description("description2");
    }

    public static Tenant getTenantRandomSampleGenerator() {
        return new Tenant().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
