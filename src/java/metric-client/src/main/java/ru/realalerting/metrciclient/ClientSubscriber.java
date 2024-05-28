package ru.realalerting.metrciclient;

import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import ru.realalerting.alertlogic.AlertLogicBase;
import ru.realalerting.producer.AlertProducer;
import ru.realalerting.protocol.MetricConstants;
import ru.realalerting.protocol.Protocol;
import ru.realalerting.subscriber.BaseSubscriber;
import ru.realalerting.subscriber.Subscriber;

import java.util.List;

public class ClientSubscriber extends BaseSubscriber {
    private final ProtocolProcessor protocolProcessor = new ProtocolProcessor();
    private int maxFragment = 1000;

    public ClientSubscriber(Subscriber consumer) {
        super(consumer);
    }

    @Override
    public void onFragment(DirectBuffer directBuffer, int offset, int length, Header header) {
        int instructionId = directBuffer.getInt(offset);
        offset += MetricConstants.ID_SIZE;
        switch (instructionId) {
            case Protocol.INSTRUCTION_SET_METRIC_ID_WITHOUT_CRITICAL_ALERTS -> {
                int[] ids = protocolProcessor.setMetricIdWithoutAlerts(directBuffer, offset, length, header);
                MetricRegistry.getInstance().changeMetric(ids[0], ids[1]);
            }
            case Protocol.INSTRUCTION_SET_METRIC_ID_WITH_CRITICAL_ALERTS -> {
                Object[] response = protocolProcessor.setMetricIdWithAlerts(directBuffer, offset, length, header);
                int requestId = (Integer) response[0];
                int metricId = (Integer) response[1];
                AlertProducer alertProducer = (AlertProducer) response[2];
                List<AlertLogicBase> alertLogicBaseList = (List<AlertLogicBase>) response[3];
                MetricRegistry.getInstance().changeMetric(requestId, metricId);
                MetricRegistry.getInstance().setCriticalAlertsToMetric(requestId, alertProducer, alertLogicBaseList);
            }
            case Protocol.INSTRUCTION_NO_CRITICAL_ALERTS_BY_METRIC_ID -> {
                int requestId = directBuffer.getInt(offset);
                offset += MetricConstants.ID_SIZE;
                MetricRegistry.getInstance().deleteRequest(requestId);
            }
            case Protocol.INSTRUCTION_SET_METRIC_CRITICAL_ALERTS_BY_METRIC_ID -> {
                Object[] response = protocolProcessor.setMetricIdWithAlerts(directBuffer, offset, length, header);
                int requestId = (Integer) response[0];
                int metricId = (Integer) response[1];
                AlertProducer alertProducer = (AlertProducer) response[2];
                List<AlertLogicBase> alertLogicBaseList = (List<AlertLogicBase>) response[3];
                MetricRegistry.getInstance().setCriticalAlertsToMetric(requestId, metricId, alertProducer, alertLogicBaseList);
            }
            case Protocol.INSTRUCTION_NEW_CRITICAL_ALERT_TYPE_1 -> {
//                Object[] response = protocolProcessor.setCriticalAlert(directBuffer, offset, length, header);
//
//                MetricRegistry.getInstance()
            }
            default -> throw new IllegalStateException("Invalid instruction id: " + instructionId);
        }
    }

    @Override
    public int doWork() {
        subscriber.getSubscription().poll(this, maxFragment);
        return 0;
    }

    @Override
    public String roleName() {
        return "client side commands receiver";
    }
}
