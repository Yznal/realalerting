package ru.realalerting.producer;

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
        buf.putInt(offset + MetricConstants.OFFSET_ID, metricId);
        buf.putLong(offset + MetricConstants.OFFSET_VALUE, value);
        buf.putLong(offset + MetricConstants.OFFSET_TIMESTAMP, timestamp);
    }

    private void sendData(int metricId, double value, long timestamp, MutableDirectBuffer buf, int offset) {
        buf.putInt(offset + MetricConstants.OFFSET_ID, metricId);
        buf.putDouble(offset + MetricConstants.OFFSET_VALUE, value);
        buf.putLong(offset + MetricConstants.OFFSET_TIMESTAMP, timestamp);
    }

    public boolean sendMetric(int metricId, long value, long timestamp) {
        boolean isSended = false;
        if (producer.getPublication().tryClaim(MetricConstants.BYTES, bufferClaim) > 0) {
            MutableDirectBuffer buf = bufferClaim.buffer();
            sendData(metricId, value, timestamp, buf, bufferClaim.offset());
            bufferClaim.commit();
            isSended = true;
        } else {
            UnsafeBuffer buf = new UnsafeBuffer(BufferUtil.allocateDirectAligned(MetricConstants.BYTES, MetricConstants.ALIGNMENT));
            sendData(metricId, value, timestamp, buf, 0);
            if (producer.getPublication().offer(buf) > 0) {
                isSended = true;
            }
        }
        return isSended;
    }

    public boolean sendMetric(int metricId, double value, long timestamp) {
        boolean isSended = false;
        if (producer.getPublication().tryClaim(MetricConstants.BYTES, bufferClaim) > 0) {
            MutableDirectBuffer buf = bufferClaim.buffer();
            sendData(metricId, value, timestamp, buf, bufferClaim.offset());
            bufferClaim.commit();
            isSended = true;
        } else {
            UnsafeBuffer buf = new UnsafeBuffer(BufferUtil.allocateDirectAligned(MetricConstants.BYTES, MetricConstants.ALIGNMENT));
            sendData(metricId, value, timestamp, buf, 0);
            if (producer.getPublication().offer(buf) > 0) {
                isSended = true;
            }
        }
        return isSended;
    }
}
