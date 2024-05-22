package ru.realalerting.controlplane.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.realalerting.controlplane.domain.AlertTestSamples.*;
import static ru.realalerting.controlplane.domain.ClientTestSamples.*;
import static ru.realalerting.controlplane.domain.MetricSubscriberTestSamples.*;
import static ru.realalerting.controlplane.domain.MetricTagsValueTestSamples.*;
import static ru.realalerting.controlplane.domain.MetricTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.realalerting.controlplane.web.rest.TestUtil;

class MetricTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Metric.class);
        Metric metric1 = getMetricSample1();
        Metric metric2 = new Metric();
        assertThat(metric1).isNotEqualTo(metric2);

        metric2.setId(metric1.getId());
        assertThat(metric1).isEqualTo(metric2);

        metric2 = getMetricSample2();
        assertThat(metric1).isNotEqualTo(metric2);
    }

    @Test
    void alertTest() throws Exception {
        Metric metric = getMetricRandomSampleGenerator();
        Alert alertBack = getAlertRandomSampleGenerator();

        metric.addAlert(alertBack);
        assertThat(metric.getAlerts()).containsOnly(alertBack);
        assertThat(alertBack.getMetric()).isEqualTo(metric);

        metric.removeAlert(alertBack);
        assertThat(metric.getAlerts()).doesNotContain(alertBack);
        assertThat(alertBack.getMetric()).isNull();

        metric.alerts(new HashSet<>(Set.of(alertBack)));
        assertThat(metric.getAlerts()).containsOnly(alertBack);
        assertThat(alertBack.getMetric()).isEqualTo(metric);

        metric.setAlerts(new HashSet<>());
        assertThat(metric.getAlerts()).doesNotContain(alertBack);
        assertThat(alertBack.getMetric()).isNull();
    }

    @Test
    void metricSubscriberTest() throws Exception {
        Metric metric = getMetricRandomSampleGenerator();
        MetricSubscriber metricSubscriberBack = getMetricSubscriberRandomSampleGenerator();

        metric.addMetricSubscriber(metricSubscriberBack);
        assertThat(metric.getMetricSubscribers()).containsOnly(metricSubscriberBack);
        assertThat(metricSubscriberBack.getMetric()).isEqualTo(metric);

        metric.removeMetricSubscriber(metricSubscriberBack);
        assertThat(metric.getMetricSubscribers()).doesNotContain(metricSubscriberBack);
        assertThat(metricSubscriberBack.getMetric()).isNull();

        metric.metricSubscribers(new HashSet<>(Set.of(metricSubscriberBack)));
        assertThat(metric.getMetricSubscribers()).containsOnly(metricSubscriberBack);
        assertThat(metricSubscriberBack.getMetric()).isEqualTo(metric);

        metric.setMetricSubscribers(new HashSet<>());
        assertThat(metric.getMetricSubscribers()).doesNotContain(metricSubscriberBack);
        assertThat(metricSubscriberBack.getMetric()).isNull();
    }

    @Test
    void clientTest() throws Exception {
        Metric metric = getMetricRandomSampleGenerator();
        Client clientBack = getClientRandomSampleGenerator();

        metric.setClient(clientBack);
        assertThat(metric.getClient()).isEqualTo(clientBack);

        metric.client(null);
        assertThat(metric.getClient()).isNull();
    }

    @Test
    void metricTagsValueTest() throws Exception {
        Metric metric = getMetricRandomSampleGenerator();
        MetricTagsValue metricTagsValueBack = getMetricTagsValueRandomSampleGenerator();

        metric.setMetricTagsValue(metricTagsValueBack);
        assertThat(metric.getMetricTagsValue()).isEqualTo(metricTagsValueBack);
        assertThat(metricTagsValueBack.getMetric()).isEqualTo(metric);

        metric.metricTagsValue(null);
        assertThat(metric.getMetricTagsValue()).isNull();
        assertThat(metricTagsValueBack.getMetric()).isNull();
    }
}
