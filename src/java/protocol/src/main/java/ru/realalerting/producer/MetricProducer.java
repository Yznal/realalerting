package ru.realalerting.producer;

import org.agrona.BufferUtil;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import ru.realalerting.protocol.Metric;
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

    public boolean sendMetric(int metricId, long value, long timestamp) {
        boolean isSended = false;
        if (producer.getPublication().tryClaim(Metric.BYTES, bufferClaim) > 0) {
            MutableDirectBuffer buf = bufferClaim.buffer();
            buf.putInt(bufferClaim.offset() + Metric.OFFSET_ID, metricId);
            buf.putLong(bufferClaim.offset() + Metric.OFFSET_VALUE, value);
            buf.putLong(bufferClaim.offset() + Metric.OFFSET_TIMESTAMP, timestamp);
            bufferClaim.commit();
            isSended = true;
        } else {
            UnsafeBuffer buf = new UnsafeBuffer(BufferUtil.allocateDirectAligned(Metric.BYTES, Metric.ALIGNMENT));
            // TODO тут смущает, кажется нужно вынести и очищать при переполнении
            buf.putInt(Metric.OFFSET_ID, metricId);
            buf.putLong(Metric.OFFSET_VALUE, value);
            buf.putLong(Metric.OFFSET_TIMESTAMP, timestamp);
            if (producer.getPublication().offer(buf) > 0) {
                isSended = true;
            }
        }
        return isSended;
    }

    public boolean sendMetric(int metricId, double value, long timestamp) {
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
