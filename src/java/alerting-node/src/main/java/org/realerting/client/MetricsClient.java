package org.realerting.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.realerting.config.AlertingNodeConfiguration;
import org.realerting.dto.Metric;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class MetricsClient {

    private final Map<Integer, Metric> metrics;

    @Inject
    MetricsClient(AlertingNodeConfiguration alertingNodeConfiguration) {
        metrics = alertingNodeConfiguration.metrics().stream()
                .collect(Collectors.toMap(Metric::id, metric -> metric));
    }

    public boolean calculateAlert(int metricId, double metricValue) {
        var metric = metrics.get(metricId);
        if (metric == null) {
            log.warn("Unknown metric {}", metricId);
            return false;
        }

        var threshold = metric.threshold();
        var needAlert = metricValue >= threshold;
        if (needAlert) {
            log.warn("Alert! For {} value {} > {}", metricId, metricValue, threshold);
        }

        return needAlert;
    }

}
