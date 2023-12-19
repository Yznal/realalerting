package org.realerting.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.realerting.dto.Metric;
import org.realerting.service.MetricAlertPublisher;
import org.realerting.service.MetricsClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MetricsClientTest {
    private static final int METRIC_ID = 1;

    private final MetricAlertPublisher publisher = mock(MetricAlertPublisher.class);
    private final List<Metric> metrics = List.of(new Metric(METRIC_ID, 100));
    private final MetricsClient metricsClient = new MetricsClient(metrics, publisher);

    @Test
    void calculateAlert() {
        var expectedValue = 100.01;
        var expectedTimeStamp = LocalDateTime.now().getNano();
        metricsClient.calculateAlert(METRIC_ID, expectedValue, expectedTimeStamp);
        verify(publisher).sendAlert(METRIC_ID, expectedValue, expectedTimeStamp);
    }

    @Test
    void calculateNoAlert() {
        metricsClient.calculateAlert(METRIC_ID, 99.99, LocalDateTime.now().getNano());
        verify(publisher, never()).sendAlert(anyInt(), anyDouble(), anyLong());
    }

    @Test
    void calculateUnknownMetric() {
        metricsClient.calculateAlert(-1, 100.01, LocalDateTime.now().getNano());
        verify(publisher, never()).sendAlert(anyInt(), anyDouble(), anyLong());
    }
}
