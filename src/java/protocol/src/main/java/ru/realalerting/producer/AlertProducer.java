package ru.realalerting.producer;

import io.aeron.logbuffer.BufferClaim;
import org.agrona.MutableDirectBuffer;
import ru.realalerting.alertlogic.AlertLogicBase;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.reader.RealAlertingConfig;

/**
 * @author Karbayev Saruar
 */
public class AlertProducer extends MetricProducer {

    public AlertProducer(Producer producer) {
        super(producer);
    }

    public AlertProducer(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo) {
        super(aeronContext, connectInfo);
    }

    public AlertProducer(RealAlertingDriverContext aeronContext, String uri, int streamId) {
        super(aeronContext, uri, streamId);
    }

    public AlertProducer(RealAlertingDriverContext aeronContext, int streamId, boolean isIpc) {
        super(aeronContext, streamId, isIpc);
    }

    public boolean sendAlert(AlertLogicBase alertLogic, int metricId, long value, long timestamp) {
        if (alertLogic.calculateAlert(metricId, value, timestamp)) {
            return sendSingleMetric(metricId, value, timestamp);
        }
        return false;
    }

    public boolean sendAlert(AlertLogicBase alertLogic, int metricId, double value, long timestamp) {
//        if (alertLogic.calculateAlert(metricId, value, timestamp)) {
            return sendSingleMetric(metricId, value, timestamp);
//        }
    }

    public boolean sendAlert(AlertLogicBase alertLogic, int alertId, int metricId, long value, long timestamp) {
        if (alertLogic.calculateAlert(metricId, value, timestamp)) {
            return sendSingleMetricWithAlertId(alertId, metricId, value, timestamp);
        }
        return false;
    }


    private void sendData(int alertId, int metricId, long value, long timestamp, MutableDirectBuffer buf, int offset) {
        buf.putInt(offset, alertId);
        offset += MetricConstants.ID_SIZE;
        buf.putInt(offset + MetricConstants.ID_OFFSET, metricId);
        buf.putLong(offset + MetricConstants.VALUE_OFFSET, value);
        buf.putLong(offset + MetricConstants.TIMESTAMP_OFFSET, timestamp);
    }

    private boolean sendSingleMetricWithAlertId(int alertId, int metricId, long value, long timestamp) {
        boolean isSended = false;
        BufferClaim curBufferClaim = this.bufferClaim.get();
        long temp = producer.getPublication().tryClaim(MetricConstants.ID_SIZE + MetricConstants.METRIC_BYTES, curBufferClaim);
//        System.out.println(temp);
        if (temp > 0) {
            MutableDirectBuffer buf = curBufferClaim.buffer();
            sendData(alertId, metricId, value, timestamp, buf, curBufferClaim.offset());
            curBufferClaim.commit();
            isSended = true;
        } else {
            ++dataLeaked;
        }
        return isSended;
    }

    public boolean sendAlertWithAlertId(AlertLogicBase alertLogic, int alertId, int metricId, long value, long timestamp) {
        if (alertLogic.calculateAlert(metricId, value, timestamp)) {
            return sendSingleMetricWithAlertId(alertId, metricId, value, timestamp);
        }
        return false;
    }

}