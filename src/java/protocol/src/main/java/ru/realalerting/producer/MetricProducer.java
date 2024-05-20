package ru.realalerting.producer;

import io.aeron.logbuffer.BufferClaim;
import org.agrona.BufferUtil;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.reader.RealAlertingConfig;

/**
 * @author Karbayev Saruar
 */
public class MetricProducer extends BaseProducer {

    public MetricProducer(Producer producer) {
        super(producer);
    }

    public MetricProducer(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo) {
        super(aeronContext, connectInfo);
    }

    public MetricProducer(RealAlertingDriverContext aeronContext, String uri, int streamId) {
        super(aeronContext, uri, streamId);
    }

    public MetricProducer(RealAlertingDriverContext aeronContext, int streamId, boolean isIpc) {
        super(aeronContext, streamId, isIpc);
    }

    private void sendData(int metricId, long value, long timestamp, MutableDirectBuffer buf, int offset) {
        buf.putInt(offset + MetricConstants.ID_OFFSET, metricId);
        buf.putLong(offset + MetricConstants.VALUE_OFFSET, value);
        buf.putLong(offset + MetricConstants.TIMESTAMP_OFFSET, timestamp);
    }

    private void sendData(int metricId, double value, long timestamp, MutableDirectBuffer buf, int offset) {
        buf.putInt(offset + MetricConstants.ID_OFFSET, metricId);
        buf.putDouble(offset + MetricConstants.VALUE_OFFSET, value);
        buf.putLong(offset + MetricConstants.TIMESTAMP_OFFSET, timestamp);
    }

    public boolean sendSingleMetric(int metricId, long value, long timestamp) {
        boolean isSended = false;
        BufferClaim curBufferClaim = this.bufferClaim.get();
        if (producer.getPublication().tryClaim(MetricConstants.METRIC_BYTES, curBufferClaim) > 0) {
            MutableDirectBuffer buf = curBufferClaim.buffer();
            sendData(metricId, value, timestamp, buf, curBufferClaim.offset());
            curBufferClaim.commit();
            isSended = true;
        } else {
            ++dataLeaked;
        }
        return isSended;
    }

    public boolean sendSingleMetric(int metricId, double value, long timestamp) {
        boolean isSended = false;
        BufferClaim curBufferClaim = this.bufferClaim.get();
        if (producer.getPublication().tryClaim(MetricConstants.METRIC_BYTES, curBufferClaim) > 0) {
            MutableDirectBuffer buf = curBufferClaim.buffer();
            sendData(metricId, value, timestamp, buf, curBufferClaim.offset());
            curBufferClaim.commit();
            isSended = true;
        } else {
            ++dataLeaked;
        }
        return isSended;
    }
}
