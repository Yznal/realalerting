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

import static org.realerting.config.AlertingNodeConstants.METRIC_TIMESTAMP_OFFSET;
import static org.realerting.config.AlertingNodeConstants.METRIC_VALUE_OFFSET;


public class MetricsSubscriber implements FragmentHandler, AutoCloseable, Runnable {
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

        channel = configuration.getChannel();
        streamId = configuration.getStreamId();
        subscription = aeron.addSubscription(channel, streamId);
        isRunning = new AtomicBoolean(false);
        idle = new SleepingIdleStrategy();

        SigInt.register(this::close);
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public void onFragment(DirectBuffer buffer, int offset, int length, Header header) {
        var id = buffer.getLong(offset);
        var value = buffer.getDouble(offset + METRIC_VALUE_OFFSET);
        var nanoTimestamp = buffer.getLong(offset + METRIC_TIMESTAMP_OFFSET);
//        log.info("MetricsSubscriber. Received id={}: {} at {}", id, value, LocalDateTime.ofInstant(Instant.ofEpochSecond(nanoTimestamp / 1_000_000_000, (int) (nanoTimestamp % 1_000_000_000)), ZoneId.systemDefault()));
        metricsClient.calculateAlert(id, value, nanoTimestamp);
    }

    public void start() {
        isRunning.set(true);

        log.info("MetricsSubscriber. Connecting to channel={}, streamId={}", channel, streamId);
        while (isRunning() && !subscription.isConnected()) {
            idle.idle();
        }

        if (isRunning()) {
            log.info("MetricsSubscriber. Ready to receive metrics at channel={}, streamId={}", channel, streamId);
        }
    }

    @Override
    public void close() {
        if (isRunning.get()) {
            log.info("MetricsSubscriber closing active publication");
            isRunning.set(false);
            subscription.close();
        }

        log.info("MetricsSubscriber closed");
    }

    @Override
    public void run() {
        log.info("MetricsSubscriber. Running");
        while (isRunning.get()) {
            var poll = subscription.poll(this, 256);
            if (poll >= 0) {
                idle.idle(poll);
            }
        }

        close();
    }
}
