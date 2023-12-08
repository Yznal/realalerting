package org.realerting.service;

import io.aeron.Aeron;
import io.aeron.Publication;
import org.agrona.BufferUtil;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SigInt;
import org.agrona.concurrent.SleepingIdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;
import org.realerting.config.AlertingNodeConfiguration;
import org.realerting.config.AlertingNodeContext;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.realerting.config.AlertingNodeConstants.*;


public class MetricAlertPublisher implements AutoCloseable {
    private static final Logger log = AlertingNodeContext.getLogger();
    private static final UnsafeBuffer BUFFER = new UnsafeBuffer(BufferUtil.allocateDirectAligned(METRIC_ID_LENGTH, ALIGNMENT));

    private final String channel;
    private final int streamId;
    private final Publication publication;
    private final AtomicBoolean isRunning;
    private final IdleStrategy idle;


    public MetricAlertPublisher(Aeron aeron,
                                AlertingNodeConfiguration.AeronConnectionConfiguration configuration) {
        channel = String.format(AERON_ENDPOINT_FORMAT, configuration.getIp(), configuration.getPort());
        streamId = configuration.getStreamId();
        publication = aeron.addPublication(channel, streamId);
        isRunning = new AtomicBoolean(false);
        SigInt.register(() -> isRunning.set(false));
        idle = new SleepingIdleStrategy();
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public void start() {
        while (!publication.isConnected()) {
            idle.idle();
        }

        isRunning.set(true);
        log.info("MetricAlertPublisher. Ready to publish alerts at channel={}, streamId={}", channel, streamId);
    }

    public void sendAlert(int metricId) {
        log.info("MetricAlertPublisher. Sending alert for {}", metricId);
        BUFFER.putInt(0, metricId);
        var publicationResult = publication.offer(BUFFER, 0, METRIC_ID_LENGTH);

        for (int i = 0; i < ATTEMPTS_TO_RESEND && publicationResult < 0; i++) {
            log.warn("MetricAlertPublisher could not send alert for metric {}. Reason code: {}", metricId, publicationResult);
            publicationResult = publication.offer(BUFFER, 0, METRIC_ID_LENGTH);
        }
    }

    @Override
    public void close() {
        log.info("MetricAlertPublisher closing active publication");
        publication.close();
    }
}
