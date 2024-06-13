package ru.realalerting.controlplane.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Client getClientSample1() {
        return new Client()
            .id(1)
            .protocolProducerAddress("protocolProducerAddress1")
            .protocolProducerPort(1)
            .protocolProducerUri("protocolProducerUri1")
            .protocolProducerStreamId(1)
            .protocolSubscriberAddress("protocolSubscriberAddress1")
            .protocolSubscriberPort(1)
            .protocolSubscriberUri("protocolSubscriberUri1")
            .protocolSubscriberStreamId(1)
            .metricProducerAddress("metricProducerAddress1")
            .metricProducerPort(1)
            .metricProducerUri("metricProducerUri1")
            .metricProducerStreamId(1);
    }

    public static Client getClientSample2() {
        return new Client()
            .id(2)
            .protocolProducerAddress("protocolProducerAddress2")
            .protocolProducerPort(2)
            .protocolProducerUri("protocolProducerUri2")
            .protocolProducerStreamId(2)
            .protocolSubscriberAddress("protocolSubscriberAddress2")
            .protocolSubscriberPort(2)
            .protocolSubscriberUri("protocolSubscriberUri2")
            .protocolSubscriberStreamId(2)
            .metricProducerAddress("metricProducerAddress2")
            .metricProducerPort(2)
            .metricProducerUri("metricProducerUri2")
            .metricProducerStreamId(2);
    }

    public static Client getClientRandomSampleGenerator() {
        return new Client()
            .id(intCount.incrementAndGet())
            .protocolProducerAddress(UUID.randomUUID().toString())
            .protocolProducerPort(intCount.incrementAndGet())
            .protocolProducerUri(UUID.randomUUID().toString())
            .protocolProducerStreamId(intCount.incrementAndGet())
            .protocolSubscriberAddress(UUID.randomUUID().toString())
            .protocolSubscriberPort(intCount.incrementAndGet())
            .protocolSubscriberUri(UUID.randomUUID().toString())
            .protocolSubscriberStreamId(intCount.incrementAndGet())
            .metricProducerAddress(UUID.randomUUID().toString())
            .metricProducerPort(intCount.incrementAndGet())
            .metricProducerUri(UUID.randomUUID().toString())
            .metricProducerStreamId(intCount.incrementAndGet());
    }
}
