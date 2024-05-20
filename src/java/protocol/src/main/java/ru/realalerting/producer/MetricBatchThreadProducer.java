package ru.realalerting.producer;

import com.lmax.disruptor.EventTranslatorThreeArg;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import io.aeron.logbuffer.BufferClaim;
import org.agrona.MutableDirectBuffer;
import ru.realalerting.alertsubscriber.clickhouse.SimpleObjectPool;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.reader.RealAlertingConfig;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MetricBatchThreadProducer extends MetricProducer {

    public MetricBatchThreadProducer(Producer producer) {
        super(producer);
    }

    public MetricBatchThreadProducer(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo) {
        super(aeronContext, connectInfo);
    }

    public MetricBatchThreadProducer(RealAlertingDriverContext aeronContext, int streamId, boolean isIpc) {
        super(aeronContext, streamId, isIpc);
    }

    public MetricBatchThreadProducer(RealAlertingDriverContext aeronContext, String uri, int streamId) {
        super(aeronContext, uri, streamId);
    }

    public static final class Metric {
        int metricId;
        long value;
        long timestamp;
    }

    static final EventTranslatorThreeArg<Metric, Integer, Long, Long>
            TRANSLATOR = (metric, sequence, metricId, timestamp, value) -> {
        metric.metricId = metricId;
        metric.timestamp = timestamp;
        metric.value = value;
    };

    private Disruptor<Metric> disruptor;
    int batchSize = 0;
    int maxBatchSize = 200;
    final Executor executor = Executors.newSingleThreadExecutor();
    int bufferSize = 8192;
    private final Map<Metric, Integer> bufferMetric = new ConcurrentHashMap<>();
    final SimpleObjectPool pool = new SimpleObjectPool<>(10, 1000,
            () -> ByteBuffer.allocate(bufferSize),
            ByteBuffer::clear);
    ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

    {
        disruptor = new Disruptor<>(
                Metric::new,
                bufferSize,
                DaemonThreadFactory.INSTANCE);


        disruptor.handleEventsWith((metric, sequence, endOfBatch) -> {
            buffer.putInt(metric.metricId);
            buffer.putLong(metric.value);
            buffer.putLong(metric.timestamp);
            batchSize++;
            if (batchSize == maxBatchSize) {
                executor.execute(() -> {
                    if (sendMetricBatch()) {
                        batchSize = 0;
                    }
                    pool.offer(buffer);
                });
                buffer = (ByteBuffer) pool.borrow();
            }
        });
        disruptor.start();
    }


    private boolean sendMetricBatch() {
        boolean isSended = false;
        BufferClaim curBufferClaim = this.bufferClaim.get();
        if (this.producer.getPublication().tryClaim(batchSize * MetricConstants.METRIC_BYTES, curBufferClaim) > 0L) {
            MutableDirectBuffer buf = curBufferClaim.buffer();
            buf.putBytes(curBufferClaim.offset(), buffer, batchSize * MetricConstants.METRIC_BYTES);
            curBufferClaim.commit();
            isSended = true;

        } else {
            ++dataLeaked;
        }
        return isSended;
    }

    // TODO override sendMetric?
    public void sendMetricBatch(int metricId, long value, long timestamp) {
        disruptor.publishEvent(TRANSLATOR, metricId, value, timestamp);
    }
}
