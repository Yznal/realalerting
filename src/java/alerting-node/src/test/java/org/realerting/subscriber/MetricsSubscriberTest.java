package org.realerting.subscriber;

import org.junit.jupiter.api.Test;
import org.realerting.service.MetricsClient;
import org.realerting.service.MetricsSubscriber;

import java.util.Random;

import static org.agrona.BitUtil.SIZE_OF_DOUBLE;
import static org.agrona.BitUtil.SIZE_OF_INT;


class MetricsSubscriberTest {
    private static final Random RANDOM = new Random(42);
    private static final int METRIC_ID_LENGTH = SIZE_OF_INT;
    private static final int METRIC_VALUE_LENGTH = SIZE_OF_DOUBLE;
    private static final int MESSAGE_LENGTH = METRIC_ID_LENGTH + METRIC_VALUE_LENGTH;

    MetricsSubscriber subscriber;

    MetricsClient metricsClient;

    @Test
    void subscribeToMetrics() {
        var expectedMetrics = 10;
//
//        // setUp publisher
//        try (var publisher = provider.getAeron().addPublication(subscriber.getChannel(), subscriber.getStreamId())) {
//            final IdleStrategy idle = new SleepingIdleStrategy();
//            while (!publisher.isConnected()) {
//                idle.idle();
//            }
//            log.info("Channel status: {}", publisher.channelStatus());
//
//            final UnsafeBuffer buffer = new UnsafeBuffer(BufferUtil.allocateDirectAligned(32, 4));
//            for (int i = 0; i < expectedMetrics; ++i) {
//                int metricId = RANDOM.nextInt(1, 4);
//                buffer.putInt(0, metricId);
//
//                var metricValue = RANDOM.nextInt(200) + RANDOM.nextDouble();
//                buffer.putDouble(METRIC_ID_LENGTH, metricValue);
//
//                log.debug("Offering {}: {}", metricId, metricValue);
//                var result = publisher.offer(buffer, 0, MESSAGE_LENGTH); // publish metrics
//                if (result < 0) {
//                    log.warn("Unsuccessful offering: {}", result);
//                }
//                Thread.sleep(1L);
//            }
//        } finally {
//            verify(metricsClient, times(expectedMetrics)).calculateAlert(anyInt(), anyDouble()); // verify they all have been calculated
//        }
    }
}