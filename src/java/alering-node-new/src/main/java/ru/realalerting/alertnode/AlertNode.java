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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AlertNode {
    private ConcurrentMap<Integer, List<Integer>> alertIdByMetricId = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, List<AlertLogicBase>> alertComputationByAlertId = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, AlertProducer> alertProducerByMetricId = new ConcurrentHashMap<>();
    private MetricSubscriber metricSubscriber;

    public AlertNode() {}

    public AlertNode(Subscriber subscriber) {
        metricSubscriber = new MetricSubscriber(subscriber) {
            @Override
            public void onFragment(DirectBuffer directBuffer, int offset, int length, Header header) {
                metricProcessing(directBuffer, offset, length, header);
            }
        };
    }

    public AlertNode(MetricSubscriber metricSubscriber) {
        this.metricSubscriber = metricSubscriber;
    }

    public void addAlertProducer(int metricId, Producer producer) {
        alertProducerByMetricId.putIfAbsent(metricId, new AlertProducer(producer));
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
        alertIdByMetricId.putIfAbsent(alertInfo.getMetricId(), new ArrayList<>());
        alertComputationByAlertId.putIfAbsent(alertInfo.getAlertId(), new ArrayList<>());

        alertIdByMetricId.get(alertInfo.getMetricId()).add(alertInfo.getAlertId());
        alertComputationByAlertId.get(alertInfo.getAlertId()).add(alertLogic);
    }

    private void metricProcessing(DirectBuffer buffer, int offset, int length, Header header) {
        int metricId = buffer.getInt(offset + MetricConstants.ID_OFFSET);
        long value = buffer.getLong(offset + MetricConstants.VALUE_OFFSET);
        long timestamp = buffer.getLong(offset + MetricConstants.TIMESTAMP_OFFSET);
        List<Integer> alertIds = alertIdByMetricId.get(metricId);
        if (alertIds != null) {
            for (Integer alertId : alertIdByMetricId.get(metricId)) {
                for (AlertLogicBase alertLogic : alertComputationByAlertId.get(alertId)) {
                    alertProducerByMetricId.get(metricId).sendAlertWithAlertId(alertLogic, alertId, metricId, value, timestamp);
                }
            }
        }
    }

    public void start() throws Exception {
//        alertProducer.waitUntilConnected();
        final AgentRunner receiveAgentRunner = new AgentRunner(metricSubscriber.getConsumer().getIdle(), Throwable::printStackTrace, null, metricSubscriber);
        AgentRunner.startOnThread(receiveAgentRunner);
    }

//    public boolean isRunning() {
//        return alertProducer.isRunning();
//    }



}
