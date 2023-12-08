package org.realerting.service;

import org.realerting.config.AlertingNodeContext;
import org.realerting.dto.Metric;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MetricsClient {
    private static final Logger log = AlertingNodeContext.getLogger();

    private final Map<Integer, Metric> metrics;
    private final MetricAlertPublisher publisher;

    public MetricsClient(List<Metric> metrics, MetricAlertPublisher publisher) {
        this.metrics = metrics.stream()
            .collect(Collectors.toMap(Metric::getId, metric -> metric));
        this.publisher = publisher;
    }

    public void calculateAlert(int metricId, double metricValue, long metricTimestamp) {
        var metric = metrics.get(metricId);
        if (metric == null) {
            log.warn("Unknown metric {}", metricId);
            return;
        }

        var threshold = metric.getThreshold();
        if (metricValue >= threshold) {
            log.info("MetricsClient calculated an alert for id={} value={} > {}", metricId, metricValue, threshold);
            publisher.sendAlert(metricId, metricValue, metricTimestamp);
        }
    }

}
