package ru.realalerting.alertnode;

import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.AgentRunner;
import ru.realalerting.alertlogic.AlertInfo;
import ru.realalerting.alertlogic.AlertLogicBase;
import ru.realalerting.producer.AlertProducer;
import ru.realalerting.producer.Producer;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.subscriber.MetricSubscriber;
import ru.realalerting.subscriber.Subscriber;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AlertNode {
    private ConcurrentMap<Integer, Integer> alertIdByMetricId = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, AlertLogicBase> alertComputationByAlertId = new ConcurrentHashMap<>();
    private AlertProducer alertProducer;
    private MetricSubscriber metricSubscriber;

    public AlertNode() {}

    public AlertNode(Producer producer, Subscriber subscriber) {
        alertProducer = new AlertProducer(producer);
        metricSubscriber = new MetricSubscriber(subscriber) {
            @Override
            public void onFragment(DirectBuffer directBuffer, int offset, int length, Header header) {
                metricProcessing(directBuffer, offset, length, header);
            }
        };
    }

    public AlertNode(AlertProducer alertProducer, MetricSubscriber metricSubscriber) {
        this.alertProducer = alertProducer;
        this.metricSubscriber = metricSubscriber;
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

    public void addAlert(AlertInfo alertInfo, AlertLogicBase alertLogic) {
        alertIdByMetricId.putIfAbsent(alertInfo.getMetricId(), alertInfo.getAlertId());
        alertComputationByAlertId.put(alertInfo.getAlertId(), alertLogic);
    }

    private void metricProcessing(DirectBuffer buffer, int offset, int length, Header header) {
        int metricId = buffer.getInt(offset + MetricConstants.ID_OFFSET);
        long value = buffer.getLong(offset + MetricConstants.VALUE_OFFSET);
        long timestamp = buffer.getLong(offset + MetricConstants.TIMESTAMP_OFFSET);
        int alertId = alertIdByMetricId.get(metricId);
        alertProducer.sendAlertWithAlertId(alertComputationByAlertId.get(alertId), alertId, metricId, value, timestamp);
    }

    public void start() throws Exception {
//        alertProducer.start();
        final AgentRunner receiveAgentRunner = new AgentRunner(metricSubscriber.getConsumer().getIdle(), Throwable::printStackTrace, null, metricSubscriber);
        AgentRunner.startOnThread(receiveAgentRunner);
    }

    public boolean isRunning() {
        return alertProducer.isRunning();
    }



}
