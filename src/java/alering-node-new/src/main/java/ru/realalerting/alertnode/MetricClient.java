package ru.realalerting.alertnode;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;


public class MetricClient {
    private final Int2ObjectOpenHashMap<AlertInfo> alerts;
    private final AlertProducer alertProducer;

    public MetricClient(List<AlertInfo> alerts, AlertProducer alertProducer ) {
        this.alerts = new Int2ObjectOpenHashMap<>(alerts.stream()
                .collect(Collectors.toMap(AlertInfo::getAlertId, alert -> alert)));
        this.alertProducer = alertProducer;
    }

    public void calculateAlert(int metricId, long value, long timestamp) {
        AlertInfo alertInfo = alerts.get(metricId);
        if (isNull(alertInfo)) {
            return;
        }

        switch (alertInfo.getComp()) {
            case GREATER -> {
                if (value > alertInfo.getThreshold()) {
                    alertProducer.sendAlert(metricId, value, timestamp);
                }
            }
            case LESS -> {
                if (value < alertInfo.getThreshold()) {
                    alertProducer.sendAlert(metricId, value, timestamp);
                }
            }
            case EQUAL -> {
                if (value == alertInfo.getThreshold()) {
                    alertProducer.sendAlert(metricId, value, timestamp);
                }
            }
            case GREATER_OR_EQUAL -> {
                if (value >= alertInfo.getThreshold()) {
                    alertProducer.sendAlert(metricId, value, timestamp);
                }
            }
            case LESS_OR_EQUAL -> {
                if (value <= alertInfo.getThreshold()) {
                    alertProducer.sendAlert(metricId, value, timestamp);
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + alertInfo.getComp());
        }
    }

}
