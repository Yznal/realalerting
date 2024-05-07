package ru.realalerting.producer;

import ru.realalerting.alertlogic.AlertLogicBase;
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
            return sendMetric(metricId, value, timestamp);
        }
        return false;
    }

    public boolean sendAlert(AlertLogicBase alertLogic, int metricId, double value, long timestamp) {
//        if (alertLogic.calculateAlert(metricId, value, timestamp)) {
            return sendMetric(metricId, value, timestamp);
//        }
    }

}