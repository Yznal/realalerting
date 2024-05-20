package ru.realalerting.protocol;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import ru.realalerting.alertlogic.AlertInfo;
import ru.realalerting.producer.AlertProducer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Karbayev Saruar
 */
public class MetricClient {
    private final Int2ObjectOpenHashMap<AlertInfo> alerts;
    private final AlertProducer alertProducer;

    public MetricClient(List<AlertInfo> alerts, AlertProducer alertProducer ) {
        this.alerts = new Int2ObjectOpenHashMap<>(alerts.stream()
                .collect(Collectors.toMap(AlertInfo::getAlertId, alert -> alert)));
        this.alertProducer = alertProducer;
    }
}
