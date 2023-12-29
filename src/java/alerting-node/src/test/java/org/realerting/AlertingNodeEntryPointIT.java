package org.realerting;

import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.BufferUtil;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingIdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.realerting.config.AlertingNodeConfiguration;
import org.realerting.config.AlertingNodeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.realerting.config.AlertingNodeConstants.*;

/**
 * E2E
 */
class AlertingNodeEntryPointIT {
    private static final Logger log = LoggerFactory.getLogger(AlertingNodeEntryPointIT.class);

    private static final UnsafeBuffer DUMMY_BUFFER = new UnsafeBuffer(BufferUtil.allocateDirectAligned(MESSAGE_LENGTH, ALIGNMENT));
    private static final Random RANDOM = new Random(42);

    static Thread runningMain;

    final IdleStrategy idle = new SleepingIdleStrategy();

    @BeforeAll
    static void start() {
        runningMain = Thread.ofPlatform().daemon(true)
            .start(() -> AlertingNodeEntryPoint.main(new String[]{"src/test/resources/application.yaml"}));
        await().atMost(5, TimeUnit.MINUTES)
            .until(() -> {
                try {
                    var context = AlertingNodeContext.getInstance();
                    return context.isRunning();
                } catch (Exception e) {
                    return false;
                }
            });
    }

    @Test
    void testE2E() {
        var configuration = AlertingNodeConfiguration.getInstance();
        var context = AlertingNodeContext.getInstance();
        var aeron = context.getAeron();

        // setUp dummy publisher
        var sConfig = configuration.getSubscriberConfiguration();
        var dummyPublisher = aeron.addPublication(sConfig.getChannel(), sConfig.getStreamId());

        // setUp dummy subscriber
        var pConfig = configuration.getPublisherConfiguration();
        var dummySubscriber = aeron.addSubscription(pConfig.getChannel(), pConfig.getStreamId());

        while (!dummyPublisher.isConnected()
            || !dummySubscriber.isConnected()
            || !AlertingNodeContext.getInstance().isRunning()) {
            idle.idle();
        }

        var latencies = new java.util.ArrayList<>(List.<Long>of());
        for (int i = 0; i < 1_000; ++i) {
            var start = System.nanoTime();

            var metricId = RANDOM.nextInt(1, 3);
            DUMMY_BUFFER.putLong(0, metricId);
            var metricValue = RANDOM.nextInt(5000, 15000) + RANDOM.nextDouble();
            DUMMY_BUFFER.putDouble(METRIC_VALUE_OFFSET, metricValue);
            var now = ZonedDateTime.of(LocalDateTime.now(), ZoneOffset.systemDefault()).toInstant();
            DUMMY_BUFFER.putLong(METRIC_TIMESTAMP_OFFSET,
                now.getEpochSecond() * 1_000_000_000 + now.getNano());
            dummyPublisher.offer(DUMMY_BUFFER, 0, MESSAGE_LENGTH);

            var poll = -1;
            while (poll <= 0) {
                FragmentHandler handler = (DirectBuffer buffer, int offset, int length, Header header) -> {
                    var alertMetricId = buffer.getInt(offset);
                    //log.info("Received alert for {} at {}", alertMetricId, LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()));
                    assertEquals(metricId, alertMetricId);
                };
                poll = dummySubscriber.poll(handler, 256);
            }
            latencies.add(System.nanoTime() - start);
        }
        assertFalse(latencies.isEmpty());

        latencies.sort(Comparator.naturalOrder());
        log.info("Fastest - {} mcs", getMicroLatency(latencies.getFirst()));
        log.info("0.5 latency - {} mcs", getLatencyPercentile(latencies, 0.5));
        log.info("0.9 latency - {} mcs", getLatencyPercentile(latencies, 0.9));
        log.info("0.95 latency - {} mcs", getLatencyPercentile(latencies, 0.95));
        log.info("0.99 latency - {} mcs", getLatencyPercentile(latencies, 0.99));
    }

    @AfterAll
    static void killMain() {
        if (runningMain != null && runningMain.isAlive()) {
            runningMain.interrupt();
        }
    }

    private double getLatencyPercentile(List<Long> nanoLatencies, double percentile) {
        var index = (int) (percentile * nanoLatencies.size());
        var nanoLatency = nanoLatencies.get(index);
        return getMicroLatency(nanoLatency);
    }

    private double getMicroLatency(Long nanoLatency) {
        var microLatency = nanoLatency / 1000.0;
        return new BigDecimal(microLatency)
            .setScale(3, RoundingMode.HALF_UP)
            .doubleValue();
    }
}
