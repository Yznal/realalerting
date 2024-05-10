package ru.realalerting.metrciclient;

import com.lmax.disruptor.EventTranslatorThreeArg;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.agrona.BufferUtil;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import ru.realalerting.alertlogic.AlertLogicBase;
import ru.realalerting.alertsubscriber.clickhouse.SimpleObjectPool;
import ru.realalerting.producer.MetricProducer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.MetricConstants;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ClientMetricProducer extends MetricProducer {

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

    private Disruptor<Metric> disruptor; // TODO возможно не нужен, т.к. publication thread safe (почитать в доках)
    int batchSize = 0;
    int maxBatchSize = 200;
    final Executor executor = Executors.newSingleThreadExecutor();
    int bufferSize = 8192;
    private final Map<Metric, Integer> bufferMetric = new ConcurrentHashMap<>();
    final SimpleObjectPool pool = new SimpleObjectPool<>(10, 1000,
            () -> ByteBuffer.allocate(bufferSize),
            ByteBuffer::clear); // TODO не нужен
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


    ClientMetricProducer(Producer metricProducer, Producer alertProducer) {
        super(metricProducer);
    }

    private boolean sendMetricBatch() {
        boolean isSended = false;
        if (this.producer.getPublication().tryClaim(batchSize * MetricConstants.BYTES, this.bufferClaim) > 0L) {
            MutableDirectBuffer buf = this.bufferClaim.buffer();
            buf.putBytes(bufferClaim.offset(), buffer, batchSize * MetricConstants.BYTES);
            this.bufferClaim.commit();
            isSended = true;

        } else { // TODO не нужен, если не получилось, то увеличиваем счетсик пролитых данных
            UnsafeBuffer buf = new UnsafeBuffer(BufferUtil.allocateDirectAligned(
                    batchSize * MetricConstants.BYTES, MetricConstants.ALIGNMENT));
            buf.putBytes(bufferClaim.offset(), buffer, batchSize * MetricConstants.BYTES);
            if (this.producer.getPublication().offer(buf) > 0L) {
                isSended = true;
            }
        }
        return isSended;
    }

    @Override
    public boolean sendMetric(int metricId, long value, long timestamp) {
        disruptor.publishEvent(TRANSLATOR, metricId, value, timestamp);
        return true;
    }

    public void sendMetric(AlertLogicBase alertLogic, int metricId, long value, long timestamp) {
        if (alertLogic.calculateAlert(metricId, value, timestamp)) {
            sendMetric(metricId, value, timestamp);
        }
    }
}
