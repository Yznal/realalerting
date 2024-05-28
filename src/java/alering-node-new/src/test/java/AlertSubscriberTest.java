import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.IdleStrategy;
import ru.realalerting.alertsubscriber.AlertSubscriber;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.reader.RealAlertingConfig;
import ru.realalerting.subscriber.Subscriber;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlertSubscriberTest extends AlertSubscriber {

    private int metricId;
    private long alertValue;
    private long alertTimestamp;
    private boolean alertArrived = false;
    private int alertCount = 0;

    public AlertSubscriberTest(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo, int alertId,
                               int metricId, long alertValue, long alertTimestamp) {
        super(aeronContext, connectInfo, alertId);
        this.metricId = metricId;
        this.alertValue = alertValue;
        this.alertTimestamp = alertTimestamp;
    }

    public AlertSubscriberTest(Subscriber subscriber, int alertId, int metricId, long alertValue, long alertTimestamp) {
        super(subscriber, alertId);
        this.metricId = metricId;
        this.alertValue = alertValue;
        this.alertTimestamp = alertTimestamp;
    }

    public AlertSubscriberTest(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo,
                               IdleStrategy idleStrategy, int alertId, int metricId, long alertValue, long alertTimestamp) {
        super(aeronContext, connectInfo, idleStrategy, alertId);
        this.metricId = metricId;
        this.alertValue = alertValue;
        this.alertTimestamp = alertTimestamp;
    }

    public boolean isAlertArrived() {
        return alertArrived;
    }

    public int getAlertCount() {
        return alertCount;
    }

    @Override
    public void onFragment(DirectBuffer directBuffer, int offset, int length, Header header) {
        for (int i = 0; i * (MetricConstants.ID_SIZE + MetricConstants.METRIC_BYTES) < length; ++i){
            int alertId = directBuffer.getInt(offset);
            offset += MetricConstants.ID_SIZE;
            int metricId = directBuffer.getInt(offset + i * MetricConstants.METRIC_BYTES + MetricConstants.ID_OFFSET);
            long value = directBuffer.getLong(offset + i * MetricConstants.METRIC_BYTES + MetricConstants.VALUE_OFFSET);
            long timestamp = directBuffer.getLong(offset + i * MetricConstants.METRIC_BYTES + MetricConstants.TIMESTAMP_OFFSET);
            if (lastTimestamp.get() < timestamp) { // дедупликация
                onAlert(alertId, metricId, value, timestamp);
                lastTimestamp.set(timestamp);
            }
        }
    }

    public void onAlert(int responseAlertId, int responseMetricId, long responseValue, long responseTimestamp) {
        ++alertCount;
        assertEquals(alertId, responseAlertId);
        assertEquals(metricId, responseMetricId);
        assertEquals(alertValue, responseValue);
        assertEquals(alertTimestamp, responseTimestamp);
        alertArrived = true;
    }
}
