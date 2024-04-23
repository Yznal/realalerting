package ru.realalerting.alert_node;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;


public class MetricClient {
    private final Map<Integer, AlertInfo> alerts;
    private final AlertProducer alertProducer;

    public MetricClient(List<AlertInfo> alerts, AlertProducer alertProducer ) {
        this.alerts = alerts.stream()
                .collect(Collectors.toMap(AlertInfo::getMetricId, metric -> metric));
        this.alertProducer = alertProducer;
    }

    public void calculateAlert(int metricId, long value, long timestamp) {
        AlertInfo alertInfo = alerts.get(metricId);
        if (isNull(alertInfo)) {
            return;
        }
        switch (alertInfo.getComp()) {
            case GREATER:
                if (value > alertInfo.getThreshold()) {
                    alertProducer.sendAlert(metricId, value, timestamp);
                }
                break;
            case LESS:
                if (value < alertInfo.getThreshold()) {
                    alertProducer.sendAlert(metricId, value, timestamp);
                }
                break;
            case EQUAL:
                if (value == alertInfo.getThreshold()) {
                    alertProducer.sendAlert(metricId, value, timestamp);
                }
                break;
            case GREATER_OR_EQUAL:
                if (value >= alertInfo.getThreshold()) {
                    alertProducer.sendAlert(metricId, value, timestamp);
                }
                break;
            case LESS_OR_EQUAL:
                if (value <= alertInfo.getThreshold()) {
                    alertProducer.sendAlert(metricId, value, timestamp);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + alertInfo.getComp());
        }
    }

}
