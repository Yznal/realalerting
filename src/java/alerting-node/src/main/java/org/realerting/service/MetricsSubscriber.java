package org.realerting.service;

import io.aeron.Aeron;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SigInt;
import org.agrona.concurrent.SleepingIdleStrategy;
import org.realerting.config.AlertingNodeConfiguration;
import org.realerting.config.AlertingNodeContext;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.agrona.BitUtil.SIZE_OF_INT;


public class MetricsSubscriber implements FragmentHandler, AutoCloseable {
    private static final Logger log = AlertingNodeContext.getLogger();

    private final MetricsClient metricsClient;
    private final String channel;
    private final int streamId;
    private final Subscription subscription;
    private final AtomicBoolean isRunning;
    private final IdleStrategy idle;


    public MetricsSubscriber(Aeron aeron,
                             MetricsClient metricsClient,
                             AlertingNodeConfiguration.AeronConnectionConfiguration configuration) {
        this.metricsClient = metricsClient;

        channel = String.format("aeron:udp?endpoint=%s:%s", configuration.getIp(), configuration.getPort());
        streamId = configuration.getStreamId();
        subscription = aeron.addSubscription(channel, streamId);
        isRunning = new AtomicBoolean(false);
        SigInt.register(() -> isRunning.set(false));
        idle = new SleepingIdleStrategy();
    }

    public void start() {
        isRunning.set(true);
        Thread.ofPlatform().daemon(true).start(() -> {
            while (!subscription.isConnected()) {
                idle.idle();
            }
            log.info("MetricsSubscriber. Subscribed to metrics at channel={}, streamId={}", channel, streamId);

            while (isRunning.get()) {
                var poll = subscription.poll(this, 256);
                if (poll < 0) {
                    log.warn("MetricsSubscriber. Polling < 0: {}", poll);
                } else {
                    idle.idle(poll);
                }
            }
            close();
        });

        log.info("MetricsSubscriber. Started");
    }

    @Override
    public void onFragment(DirectBuffer buffer, int offset, int length, Header header) {
        var metricId = buffer.getInt(offset);
        var metricValue = buffer.getDouble(offset + SIZE_OF_INT);
        log.info("MetricsSubscriber. Received id={}: {}", metricId, metricValue);
        metricsClient.calculateAlert(metricId, metricValue);
    }

    @Override
    public void close() {
        log.info("MetricsSubscriber closing active subscription");
        subscription.close();
    }
}
