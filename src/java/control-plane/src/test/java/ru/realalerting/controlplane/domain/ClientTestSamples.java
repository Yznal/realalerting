package ru.realalerting.controlplane.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ClientTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Client getClientSample1() {
        return new Client()
            .id(1L)
            .protocolAddress("protocolAddress1")
            .protocolPort(1)
            .protocolUri("protocolUri1")
            .protocolStreamId(1)
            .metricProducerAddress("metricProducerAddress1")
            .metricProducerPort(1)
            .metricProducerUri("metricProducerUri1")
            .metricProducerStreamId(1);
    }

    public static Client getClientSample2() {
        return new Client()
            .id(2L)
            .protocolAddress("protocolAddress2")
            .protocolPort(2)
            .protocolUri("protocolUri2")
            .protocolStreamId(2)
            .metricProducerAddress("metricProducerAddress2")
            .metricProducerPort(2)
            .metricProducerUri("metricProducerUri2")
            .metricProducerStreamId(2);
    }

    public static Client getClientRandomSampleGenerator() {
        return new Client()
            .id(longCount.incrementAndGet())
            .protocolAddress(UUID.randomUUID().toString())
            .protocolPort(intCount.incrementAndGet())
            .protocolUri(UUID.randomUUID().toString())
            .protocolStreamId(intCount.incrementAndGet())
            .metricProducerAddress(UUID.randomUUID().toString())
            .metricProducerPort(intCount.incrementAndGet())
            .metricProducerUri(UUID.randomUUID().toString())
            .metricProducerStreamId(intCount.incrementAndGet());
    }
}
