package ru.realalerting.alertnode;

import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import ru.realalerting.alertlogic.AlertLogicBase;
import ru.realalerting.producer.AlertProducer;
import ru.realalerting.producer.Producer;
import ru.realalerting.subscriber.MetricSubscriber;
import ru.realalerting.subscriber.Subscriber;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AlertNode {
    private ConcurrentMap<Integer, Integer> alertIdByMetricId;
    private ConcurrentMap<Integer, AlertLogicBase> alertComputationByAlertId = new ConcurrentHashMap<>();
    private AlertProducer alertProducer;
    private MetricSubscriber metricSubscriber;

    public AlertNode(AlertLogicBase alertLogic, AlertProducer alertProducer) {
        this.alertLogic = alertLogic;
        this.alertProducer = alertProducer;
    }

    public void setAlertProducer(Producer alertProducer) {
        this.alertProducer = new AlertProducer(alertProducer);
    }

    public void setMetricSubscriber(Subscriber metricSubscriber) {
        this.metricSubscriber = new MetricSubscriber(metricSubscriber) {
            @Override
            public void onFragment(DirectBuffer directBuffer, int offset, int length, Header header) {
                metricProcessing(directBuffer, offset, length, header);
            }
        };
    }

    private void metricProcessing(DirectBuffer buffer, int offset, int length, Header header) {
        // TODO get alertId by metricId
        int alertId
        alertProducer.sendAlertWithAlertId(alertLogic, )
    }

    public boolean sendAlert(int metricId, long value, long timestamp) {
        return alertProducer.sendAlert(alertLogic, metricId, value, timestamp); // TODO отправлять alertId еще
    }

    public void start() throws Exception {
        alertProducer.start();
    }

    public boolean isRunning() {
        return alertProducer.isRunning();
    }



}
