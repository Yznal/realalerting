package ru.realalerting.alertnode;

import io.aeron.logbuffer.BufferClaim;
import org.agrona.BufferUtil;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.Metric;
import ru.realalerting.protocol.RealAlertingConfig;
import ru.realalerting.protocol.RealAlertingDriverContext;

import java.util.concurrent.atomic.AtomicBoolean;

public class AlertProducer {
    private final Producer producer;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final BufferClaim bufferClaim = new BufferClaim();


    public AlertProducer(Producer producer) {
        this.producer = producer;
    }

    public AlertProducer(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo) {
        producer = new Producer(aeronContext, connectInfo);
    }

    public AlertProducer(RealAlertingDriverContext aeronContext, String uri, int streamId) {
        producer = new Producer(aeronContext, new RealAlertingConfig(uri, streamId));
    }

    public AlertProducer(RealAlertingDriverContext aeronContext, int streamId, boolean isIpc) {
        producer = new Producer(aeronContext, new RealAlertingConfig(streamId, isIpc));
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public void start(int retries) throws Exception {
        isRunning.set(true);
        if (!producer.waitUntilConnected(retries)) {
            throw new Exception("Cannot connect to channel");
        }
    }

    public void start() throws Exception {
        start(3);
    }

    public boolean sendAlert(int metricId, long timestamp, long value) {
        boolean isSended = false;
        if (producer.getPublication().tryClaim(Metric.BYTES, bufferClaim) > 0) {
            MutableDirectBuffer buf = bufferClaim.buffer();
            buf.putInt(bufferClaim.offset() + Metric.OFFSET_ID, metricId);
            buf.putLong(bufferClaim.offset() + Metric.OFFSET_VALUE, value);
            buf.putLong(bufferClaim.offset() + Metric.OFFSET_TIMESTAMP, timestamp);
            bufferClaim.commit();
            isSended =  true;
        } else {
            UnsafeBuffer buf = new UnsafeBuffer(BufferUtil.allocateDirectAligned(Metric.BYTES, Metric.ALIGNMENT));
            buf.putInt(Metric.OFFSET_ID, metricId);
            buf.putLong(Metric.OFFSET_VALUE, value);
            buf.putLong(Metric.OFFSET_TIMESTAMP, timestamp);
            if (producer.getPublication().offer(buf) > 0) {
                isSended = true;
            }
        }
        return isSended;
    }

    public boolean sendAlert(int metricId, long timestamp, double value) {
        boolean isSended = false;
        if (producer.getPublication().tryClaim(Metric.BYTES, bufferClaim) > 0) {
            MutableDirectBuffer buf = bufferClaim.buffer();
            buf.putInt(bufferClaim.offset() + Metric.OFFSET_ID, metricId);
            buf.putDouble(bufferClaim.offset() + Metric.OFFSET_VALUE, value);
            buf.putLong(bufferClaim.offset() + Metric.OFFSET_TIMESTAMP, timestamp);
            bufferClaim.commit();
            isSended = true;
        } else {
            UnsafeBuffer buf = new UnsafeBuffer(BufferUtil.allocateDirectAligned(Metric.BYTES, Metric.ALIGNMENT));
            buf.putInt(Metric.OFFSET_ID, metricId);
            buf.putDouble(Metric.OFFSET_VALUE, value);
            buf.putLong(Metric.OFFSET_TIMESTAMP, timestamp);
            if (producer.getPublication().offer(buf) > 0) {
                isSended = true;
            }
        }
        return isSended;
    }

}
