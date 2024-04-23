package ru.realalerting.alert_node;

import io.aeron.logbuffer.BufferClaim;
import org.agrona.MutableDirectBuffer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.AeronContext;
import ru.realalerting.protocol.ConnectInfo;
import ru.realalerting.protocol.Metric;

import java.util.concurrent.atomic.AtomicBoolean;

public class AlertProducer {
    private final Producer producer;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final BufferClaim bufferClaim = new BufferClaim();

    public AlertProducer(Producer producer) {
        this.producer = producer;
    }

    public AlertProducer(AeronContext aeronContext, ConnectInfo connectInfo) {
        producer = new Producer(aeronContext, connectInfo);
    }

    public AlertProducer(AeronContext aeronContext, String uri, int streamId) {
        producer = new Producer(aeronContext, new ConnectInfo(uri, streamId));
    }

    public AlertProducer(AeronContext aeronContext, int streamId, boolean isIpc) {
        producer = new Producer(aeronContext, new ConnectInfo(streamId, isIpc));
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

    private boolean checkPublicationClaim(int retries) {
        int index = Math.toIntExact(producer.getPublication().tryClaim(Metric.BYTES, bufferClaim));
        for (int i = 0; i < retries && index < 0; ++i) {
            switch (index) {
                case -1:
                    try {
                        start(3);
                    } catch (Exception e) {
                        return false;
                    }
                    break;
                case -4:
                    return false;
                default:
                    index = Math.toIntExact(producer.getPublication().tryClaim(Metric.BYTES, bufferClaim));
                    break;
            }
        }
        return index > 0;
    }

    public boolean sendAlert(int metricId, long timestamp, long value, int retries) {
        boolean success = checkPublicationClaim(retries);
        if (success) {
            MutableDirectBuffer buf = bufferClaim.buffer();
            buf.putInt(Metric.OFFSET_ID, metricId);
            buf.putLong(Metric.OFFSET_VALUE, value);
            buf.putLong(Metric.OFFSET_TIMESTAMP, timestamp);
            bufferClaim.commit();
        }
        return success;
    }

    public boolean sendAlert(int metricId, long timestamp, long value) {
        return sendAlert(metricId, timestamp, value, 3);
    }

    public boolean sendAlert(int metricId, long timestamp, double value) {
        return sendAlert(metricId, timestamp, value, 3);
    }

    public boolean sendAlert(int metricId, long timestamp, double value, int retries) {
        boolean succes = checkPublicationClaim(retries);
        if (succes) {
            MutableDirectBuffer buf = bufferClaim.buffer();
            buf.putInt(Metric.OFFSET_ID, metricId);
            buf.putDouble(Metric.OFFSET_VALUE, value);
            buf.putLong(Metric.OFFSET_TIMESTAMP, timestamp);
            bufferClaim.commit();
        }
        return succes;
    }

}
