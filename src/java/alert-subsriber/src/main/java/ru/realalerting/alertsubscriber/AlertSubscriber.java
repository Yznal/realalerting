package ru.realalerting.alertsubscriber;

import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.IdleStrategy;
import ru.realalerting.subscriber.MetricSubscriber;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.reader.RealAlertingConfig;
import ru.realalerting.subscriber.Subscriber;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Karbayev Saruar
 */
public abstract class AlertSubscriber extends MetricSubscriber {
    protected int alertId;
    protected AtomicLong lastTimestamp = new AtomicLong();

    public AlertSubscriber(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo,
                           IdleStrategy idleStrategy, int alertId) {
        super(aeronContext, connectInfo, idleStrategy);
        this.alertId = alertId;
    }

    public AlertSubscriber(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo, int alertId) {
        super(aeronContext, connectInfo);
        this.alertId = alertId;
    }

    public AlertSubscriber(Subscriber subscriber, int alertId) {
        super(subscriber);
        this.alertId = alertId;
    }

    public int getAlertId() {
        return alertId;
    }

    abstract public void onAlert(int metricId, long value, long timestamp);

    @Override
    public void onFragment(DirectBuffer directBuffer, int offset, int length, Header header) {
        for (int i = 0; i * MetricConstants.METRIC_BYTES < length; ++i){
            int id = directBuffer.getInt(offset + i * MetricConstants.METRIC_BYTES + MetricConstants.ID_OFFSET);
            long value = directBuffer.getLong(offset + i * MetricConstants.METRIC_BYTES + MetricConstants.VALUE_OFFSET);
            long timestamp = directBuffer.getLong(offset + i * MetricConstants.METRIC_BYTES + MetricConstants.TIMESTAMP_OFFSET);
            if (lastTimestamp.get() < timestamp) { // дедупликация
                onAlert(id, value, timestamp);
                lastTimestamp.set(timestamp);
            }
        }
    }

}
