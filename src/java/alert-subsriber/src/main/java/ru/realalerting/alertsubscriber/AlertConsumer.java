package ru.realalerting.alertsubscriber;

import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.IdleStrategy;
import ru.realalerting.consumer.MetricConsumer;
import ru.realalerting.protocol.Metric;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.reader.RealAlertingConfig;

/**
 * @author Karbayev Saruar
 */
public abstract class AlertConsumer extends MetricConsumer {
    protected int alertId;

    public AlertConsumer(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo,
                         IdleStrategy idleStrategy, int alertId) {
        super(aeronContext, connectInfo, idleStrategy);
        this.alertId = alertId;
    }

    public int getAlertId() {
        return alertId;
    }

    abstract public void onAlert(int metricId, long value, long timestamp);

    @Override
    public void onFragment(DirectBuffer directBuffer, int offset, int length, Header header) {
        for (int i = 0; i * Metric.BYTES < length; ++i){
            int id = directBuffer.getInt(offset + i * Metric.BYTES + Metric.OFFSET_ID);
            long value = directBuffer.getLong(offset + i * Metric.BYTES + Metric.OFFSET_VALUE);
            long timestamp = directBuffer.getLong(offset + i * Metric.BYTES + Metric.OFFSET_TIMESTAMP);
            onAlert(id, value, timestamp);
            // TODO дедупликация алертов от двух AlertNode
        }
    }

}
