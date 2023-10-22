package org.realerting.subscriber;

import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SigInt;
import org.agrona.concurrent.SleepingIdleStrategy;
import org.realerting.client.MetricsClient;
import org.realerting.config.AlertingNodeConfiguration;
import org.realerting.config.MediaDriverProvider;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.agrona.BitUtil.SIZE_OF_INT;

@Slf4j
@ApplicationScoped
public class MetricsSubscriber implements FragmentHandler, AutoCloseable {

    private final MediaDriverProvider mediaDriverProvider;
    private final MetricsClient metricsClient;

    @Getter
    private final String channel;
    @Getter
    private final int streamId;
    @Getter
    private final AtomicBoolean isRunning;
    private final IdleStrategy idle;

    private Subscription incomingMetricsSubscription;

    @Inject
    MetricsSubscriber(MediaDriverProvider mediaDriverProvider,
                      MetricsClient metricsClient,
                      AlertingNodeConfiguration alertingNodeConfiguration) {
        this.mediaDriverProvider = mediaDriverProvider;
        this.metricsClient = metricsClient;

        var subscriberConfiguration = alertingNodeConfiguration.subscriber();
        channel = String.format("aeron:udp?endpoint=%s:%s", subscriberConfiguration.ip(), subscriberConfiguration.port());
        streamId = subscriberConfiguration.streamId();
        isRunning = new AtomicBoolean(false);
        SigInt.register(() -> isRunning.set(false));
        idle = new SleepingIdleStrategy();
    }

    void startUp(@Observes StartupEvent event) {
        incomingMetricsSubscription = mediaDriverProvider.getAeron().addSubscription(channel, streamId);

        isRunning.set(true);
        Thread.ofPlatform().daemon(true).start(() -> {
            while (!incomingMetricsSubscription.isConnected()) {
                idle.idle();
            }

            while (isRunning.get()) {
                var poll = incomingMetricsSubscription.poll(this, 256);
                if (poll < 0) {
                    log.warn("Polling < 0: {}", poll);
                } else {
                    idle.idle(poll);
                }
            }
            close();
        });

        log.info("MetricsSubscriber. Subscribed to metrics at channel={}, streamId={}", channel, streamId);
    }

    @Override
    public void onFragment(DirectBuffer buffer, int offset, int length, Header header) {
        var metricId = buffer.getInt(offset);
        var metricValue = buffer.getDouble(offset + SIZE_OF_INT);
        log.info("Received id={}: {}", metricId, metricValue);
        metricsClient.calculateAlert(metricId, metricValue);
    }

    @Override
    public void close() {
        log.info("MetricsSubscriber closing active subscription");
        incomingMetricsSubscription.close();
    }
}
